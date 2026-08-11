package com.boquitassanas.ortocitas.service;

import com.boquitassanas.ortocitas.dto.*;
import com.boquitassanas.ortocitas.model.*;
import com.boquitassanas.ortocitas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final int DURACION_CITA_MINUTOS = 30;

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final HorarioDisponibleRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final EstadoHorarioRepository estadoHorarioRepository;
    private final EstadoCitaRepository estadoCitaRepository;

    @Transactional(readOnly = true)
    public Cita consultarCitaPendiente(String cedula) {
        return citaRepository
                .findFirstByPaciente_CedulaAndEstado_CodigoIgnoreCaseOrderByHorario_FechaAsc(cedula, "PENDIENTE")
                .orElseThrow(() -> new NoSuchElementException("No se encontró una cita pendiente para esta cédula"));
    }

    @Transactional(readOnly = true)
    public List<HorarioDisponible> horariosDisponibles(LocalDate fecha) {
        return horarioRepository.findByFechaAndEstado_CodigoIgnoreCaseOrderByHoraInicioAsc(fecha, "DISPONIBLE");
    }

    @Transactional
    public Cita registrarCita(RegistrarCitaRequest request) {
        Paciente paciente = pacienteRepository.findByCedula(request.cedula())
                .orElseGet(() -> pacienteRepository.save(Paciente.builder()
                        .cedula(request.cedula())
                        .nombres(request.nombres())
                        .apellidos(request.apellidos())
                        .telefono(request.telefono())
                        .build()));

        HorarioDisponible horario = obtenerHorario(request.horarioId());
        comprobarEstadoHorario(horario, "DISPONIBLE");

        horario.setEstado(estadoHorario("OCUPADO"));
        horarioRepository.save(horario);

        Cita cita = Cita.builder()
                .paciente(paciente)
                .odontologo(horario.getOdontologo())
                .horario(horario)
                .estado(estadoCita("PENDIENTE"))
                .build();

        return citaRepository.save(cita);
    }

    @Transactional
    public List<HorarioDisponible> habilitarHorarios(UUID odontologoId, HabilitarHorariosRequest request) {
        Usuario odontologo = usuarioRepository.findById(odontologoId)
                .orElseThrow(() -> new NoSuchElementException("Odontólogo no encontrado"));

        if (request.fecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden habilitar horarios en fechas pasadas");
        }
        if (!request.horaInicioJornada().isBefore(request.horaFinJornada())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        EstadoHorario disponible = estadoHorario("DISPONIBLE");
        List<HorarioDisponible> nuevos = new ArrayList<>();
        LocalTime cursor = request.horaInicioJornada();

        while (cursor.plusMinutes(DURACION_CITA_MINUTOS).compareTo(request.horaFinJornada()) <= 0) {
            LocalTime fin = cursor.plusMinutes(DURACION_CITA_MINUTOS);
            nuevos.add(HorarioDisponible.builder()
                    .odontologo(odontologo)
                    .fecha(request.fecha())
                    .horaInicio(cursor)
                    .horaFin(fin)
                    .estado(disponible)
                    .build());
            cursor = fin;
        }
        return horarioRepository.saveAll(nuevos);
    }

    @Transactional
    public HorarioDisponible actualizarHorario(UUID horarioId, String nuevoEstado) {
        HorarioDisponible horario = obtenerHorario(horarioId);

        if (horario.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se pueden modificar horarios de fechas pasadas");
        }

        horario.setEstado(estadoHorario(nuevoEstado));
        return horarioRepository.save(horario);
    }

    @Transactional
    public void eliminarHorario(UUID horarioId) {
        HorarioDisponible horario = obtenerHorario(horarioId);

        if (horario.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se pueden eliminar horarios de fechas pasadas");
        }
        if (horario.getEstado() != null && "OCUPADO".equalsIgnoreCase(horario.getEstado().getCodigo())) {
            throw new IllegalStateException("No se puede eliminar un horario que ya tiene una cita asociada");
        }
        horarioRepository.delete(horario);
    }

    @Transactional(readOnly = true)
    public List<HorarioDisponible> horariosDeOdontologo(UUID odontologoId, LocalDate fecha) {
        return horarioRepository.findByOdontologoIdAndFechaOrderByHoraInicioAsc(odontologoId, fecha);
    }

    @Transactional(readOnly = true)
    public List<Cita> listarPendientes() {
        return citaRepository.findByEstado_CodigoIgnoreCaseOrderByHorario_FechaAscHorario_HoraInicioAsc("PENDIENTE");
    }

    @Transactional(readOnly = true)
    public List<Cita> listarTodas() {
        return citaRepository.findAllByOrderByHorario_FechaDescHorario_HoraInicioDesc();
    }

    @Transactional(readOnly = true)
    public Cita obtenerPorId(UUID citaId) {
        return citaRepository.findById(citaId)
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Cita> buscarPorPacienteNombreOCedula(String texto) {
        return citaRepository
                .findByPaciente_NombresContainingIgnoreCaseOrPaciente_ApellidosContainingIgnoreCaseOrPaciente_CedulaContainingOrderByHorario_FechaDesc(
                        texto, texto, texto);
    }

    @Transactional
    public Cita crearCitaManual(CrearCitaInternaRequest request) {
        Paciente paciente = pacienteRepository.findByCedula(request.cedula())
                .orElseGet(() -> pacienteRepository.save(Paciente.builder()
                        .cedula(request.cedula())
                        .nombres(request.nombresPaciente())
                        .apellidos(request.apellidosPaciente())
                        .telefono(request.telefonoPaciente())
                        .build()));

        HorarioDisponible horario = obtenerHorario(request.horarioId());
        comprobarEstadoHorario(horario, "DISPONIBLE");
        horario.setEstado(estadoHorario("OCUPADO"));
        horarioRepository.save(horario);

        Servicio servicio = null;
        if (request.servicioId() != null) {
            servicio = servicioRepository.findById(request.servicioId())
                    .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));
        }

        Cita cita = Cita.builder()
                .paciente(paciente)
                .odontologo(horario.getOdontologo())
                .horario(horario)
                .servicio(servicio)
                .estado(estadoCita("PENDIENTE"))
                .notas(request.notas())
                .build();

        return citaRepository.save(cita);
    }

    @Transactional
    public Cita actualizarCita(UUID citaId, ActualizarCitaRequest request) {
        Cita cita = obtenerPorId(citaId);

        if (request.nuevoHorarioId() != null && !request.nuevoHorarioId().equals(cita.getHorario().getId())) {
            HorarioDisponible nuevoHorario = obtenerHorario(request.nuevoHorarioId());
            comprobarEstadoHorario(nuevoHorario, "DISPONIBLE");

            if (nuevoHorario.getFecha().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reprogramar una cita a una fecha pasada");
            }

            HorarioDisponible horarioAnterior = cita.getHorario();
            horarioAnterior.setEstado(estadoHorario("DISPONIBLE"));
            horarioRepository.save(horarioAnterior);

            nuevoHorario.setEstado(estadoHorario("OCUPADO"));
            horarioRepository.save(nuevoHorario);

            cita.setHorario(nuevoHorario);
            cita.setOdontologo(nuevoHorario.getOdontologo());
        }

        if (request.servicioId() != null) {
            cita.setServicio(servicioRepository.findById(request.servicioId())
                    .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado")));
        }

        if (request.estado() != null) {
            cita.setEstado(estadoCita(request.estado()));
        }

        if (request.notas() != null) {
            cita.setNotas(request.notas());
        }

        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cambiarEstado(UUID citaId, String nuevoEstado) {
        Cita cita = obtenerPorId(citaId);
        cita.setEstado(estadoCita(nuevoEstado));
        return citaRepository.save(cita);
    }

    @Transactional
    public void eliminarCita(UUID citaId) {
        Cita cita = obtenerPorId(citaId);
        HorarioDisponible horario = cita.getHorario();
        horario.setEstado(estadoHorario("DISPONIBLE"));
        horarioRepository.save(horario);
        citaRepository.delete(cita);
    }

    private HorarioDisponible obtenerHorario(UUID id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Horario no encontrado"));
    }

    private EstadoHorario estadoHorario(String codigo) {
        return estadoHorarioRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new IllegalArgumentException("Estado de horario no válido: " + codigo));
    }

    private EstadoCita estadoCita(String codigo) {
        return estadoCitaRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new IllegalArgumentException("Estado de cita no válido: " + codigo));
    }

    private void comprobarEstadoHorario(HorarioDisponible horario, String esperado) {
        if (horario.getEstado() == null || !esperado.equalsIgnoreCase(horario.getEstado().getCodigo())) {
            throw new IllegalStateException("El horario seleccionado ya no está disponible");
        }
    }
}

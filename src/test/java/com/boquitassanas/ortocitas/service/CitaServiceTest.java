package com.boquitassanas.ortocitas.service;

import com.boquitassanas.ortocitas.dto.HabilitarHorariosRequest;
import com.boquitassanas.ortocitas.model.*;
import com.boquitassanas.ortocitas.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private PacienteRepository pacienteRepository;
    @Mock private HorarioDisponibleRepository horarioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ServicioRepository servicioRepository;
    @Mock private EstadoHorarioRepository estadoHorarioRepository;
    @Mock private EstadoCitaRepository estadoCitaRepository;

    @InjectMocks
    private CitaService citaService;

    @Test
    void habilitarHorarios_generaBloquesDeTreintaMinutos() {
        UUID odontologoId = UUID.randomUUID();
        Usuario odontologo = Usuario.builder().id(odontologoId).nombres("Buenaventura").apellidos("Vargas").build();
        EstadoHorario disponible = EstadoHorario.builder().codigo("DISPONIBLE").nombre("Disponible").build();

        when(usuarioRepository.findById(odontologoId)).thenReturn(Optional.of(odontologo));
        when(estadoHorarioRepository.findByCodigoIgnoreCase("DISPONIBLE")).thenReturn(Optional.of(disponible));
        when(horarioRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        HabilitarHorariosRequest request = new HabilitarHorariosRequest(
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(9, 0)
        );

        List<HorarioDisponible> horarios = citaService.habilitarHorarios(odontologoId, request);

        assertThat(horarios).hasSize(2);
        assertThat(horarios.get(0).getHoraInicio()).isEqualTo(LocalTime.of(8, 0));
        assertThat(horarios.get(1).getHoraFin()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void habilitarHorarios_rechazaFechaPasada() {
        UUID odontologoId = UUID.randomUUID();
        when(usuarioRepository.findById(odontologoId))
                .thenReturn(Optional.of(Usuario.builder().id(odontologoId).build()));

        HabilitarHorariosRequest request = new HabilitarHorariosRequest(
                LocalDate.now().minusDays(1), LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertThatThrownBy(() -> citaService.habilitarHorarios(odontologoId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fechas pasadas");
    }
}

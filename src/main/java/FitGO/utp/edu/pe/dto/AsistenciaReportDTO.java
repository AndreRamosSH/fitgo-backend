package FitGO.utp.edu.pe.dto;

import java.util.List;

public class AsistenciaReportDTO {
    private Long totalIngresos;
    private Double promedioDiario;
    private String diaMasConcurrido;
    private Long miembrosAusentes;
    private List<IngresoDiaDTO> ingresosPorDia;
    private List<AsistenciaDiaSemanaDTO> asistenciasPorDiaSemana;
    private List<FiltroDTO> mesesDisponibles;
    private List<FiltroDTO> miembrosDisponibles;

    public AsistenciaReportDTO() {}

    public AsistenciaReportDTO(Long totalIngresos, Double promedioDiario, String diaMasConcurrido, Long miembrosAusentes,
                               List<IngresoDiaDTO> ingresosPorDia, List<AsistenciaDiaSemanaDTO> asistenciasPorDiaSemana,
                               List<FiltroDTO> mesesDisponibles, List<FiltroDTO> miembrosDisponibles) {
        this.totalIngresos = totalIngresos;
        this.promedioDiario = promedioDiario;
        this.diaMasConcurrido = diaMasConcurrido;
        this.miembrosAusentes = miembrosAusentes;
        this.ingresosPorDia = ingresosPorDia;
        this.asistenciasPorDiaSemana = asistenciasPorDiaSemana;
        this.mesesDisponibles = mesesDisponibles;
        this.miembrosDisponibles = miembrosDisponibles;
    }

    public Long getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Long totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Double getPromedioDiario() {
        return promedioDiario;
    }

    public void setPromedioDiario(Double promedioDiario) {
        this.promedioDiario = promedioDiario;
    }

    public String getDiaMasConcurrido() {
        return diaMasConcurrido;
    }

    public void setDiaMasConcurrido(String diaMasConcurrido) {
        this.diaMasConcurrido = diaMasConcurrido;
    }

    public Long getMiembrosAusentes() {
        return miembrosAusentes;
    }

    public void setMiembrosAusentes(Long miembrosAusentes) {
        this.miembrosAusentes = miembrosAusentes;
    }

    public List<IngresoDiaDTO> getIngresosPorDia() {
        return ingresosPorDia;
    }

    public void setIngresosPorDia(List<IngresoDiaDTO> ingresosPorDia) {
        this.ingresosPorDia = ingresosPorDia;
    }

    public List<AsistenciaDiaSemanaDTO> getAsistenciasPorDiaSemana() {
        return asistenciasPorDiaSemana;
    }

    public void setAsistenciasPorDiaSemana(List<AsistenciaDiaSemanaDTO> asistenciasPorDiaSemana) {
        this.asistenciasPorDiaSemana = asistenciasPorDiaSemana;
    }

    public List<FiltroDTO> getMesesDisponibles() {
        return mesesDisponibles;
    }

    public void setMesesDisponibles(List<FiltroDTO> mesesDisponibles) {
        this.mesesDisponibles = mesesDisponibles;
    }

    public List<FiltroDTO> getMiembrosDisponibles() {
        return miembrosDisponibles;
    }

    public void setMiembrosDisponibles(List<FiltroDTO> miembrosDisponibles) {
        this.miembrosDisponibles = miembrosDisponibles;
    }

    public static class IngresoDiaDTO {
        private String dia;
        private Long valor;
        private boolean destacado;

        public IngresoDiaDTO() {}

        public IngresoDiaDTO(String dia, Long valor, boolean destacado) {
            this.dia = dia;
            this.valor = valor;
            this.destacado = destacado;
        }

        public String getDia() {
            return dia;
        }

        public void setDia(String dia) {
            this.dia = dia;
        }

        public Long getValor() {
            return valor;
        }

        public void setValor(Long valor) {
            this.valor = valor;
        }

        public boolean isDestacado() {
            return destacado;
        }

        public void setDestacado(boolean destacado) {
            this.destacado = destacado;
        }
    }

    public static class AsistenciaDiaSemanaDTO {
        private String dia;
        private Long valor;
        private Double porcentaje;

        public AsistenciaDiaSemanaDTO() {}

        public AsistenciaDiaSemanaDTO(String dia, Long valor, Double porcentaje) {
            this.dia = dia;
            this.valor = valor;
            this.porcentaje = porcentaje;
        }

        public String getDia() {
            return dia;
        }

        public void setDia(String dia) {
            this.dia = dia;
        }

        public Long getValor() {
            return valor;
        }

        public void setValor(Long valor) {
            this.valor = valor;
        }

        public Double getPorcentaje() {
            return porcentaje;
        }

        public void setPorcentaje(Double porcentaje) {
            this.porcentaje = porcentaje;
        }
    }

    public static class FiltroDTO {
        private String valor;
        private String texto;

        public FiltroDTO() {}

        public FiltroDTO(String valor, String texto) {
            this.valor = valor;
            this.texto = texto;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }
    }
}

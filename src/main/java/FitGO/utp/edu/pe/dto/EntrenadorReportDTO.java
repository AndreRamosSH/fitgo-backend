package FitGO.utp.edu.pe.dto;

import java.util.List;

public class EntrenadorReportDTO {
    private Long totalEntrenadores;
    private Long totalRutinasCreadas;
    private String masMiembros;
    private Double promedioAlumnos;
    private List<ComparativaEntrenadorDTO> comparativaEntrenadores;

    public EntrenadorReportDTO() {}

    public EntrenadorReportDTO(Long totalEntrenadores, Long totalRutinasCreadas, String masMiembros,
                               Double promedioAlumnos, List<ComparativaEntrenadorDTO> comparativaEntrenadores) {
        this.totalEntrenadores = totalEntrenadores;
        this.totalRutinasCreadas = totalRutinasCreadas;
        this.masMiembros = masMiembros;
        this.promedioAlumnos = promedioAlumnos;
        this.comparativaEntrenadores = comparativaEntrenadores;
    }

    public Long getTotalEntrenadores() {
        return totalEntrenadores;
    }

    public void setTotalEntrenadores(Long totalEntrenadores) {
        this.totalEntrenadores = totalEntrenadores;
    }

    public Long getTotalRutinasCreadas() {
        return totalRutinasCreadas;
    }

    public void setTotalRutinasCreadas(Long totalRutinasCreadas) {
        this.totalRutinasCreadas = totalRutinasCreadas;
    }

    public String getMasMiembros() {
        return masMiembros;
    }

    public void setMasMiembros(String masMiembros) {
        this.masMiembros = masMiembros;
    }

    public Double getPromedioAlumnos() {
        return promedioAlumnos;
    }

    public void setPromedioAlumnos(Double promedioAlumnos) {
        this.promedioAlumnos = promedioAlumnos;
    }

    public List<ComparativaEntrenadorDTO> getComparativaEntrenadores() {
        return comparativaEntrenadores;
    }

    public void setComparativaEntrenadores(List<ComparativaEntrenadorDTO> comparativaEntrenadores) {
        this.comparativaEntrenadores = comparativaEntrenadores;
    }

    public static class ComparativaEntrenadorDTO {
        private String entrenador;
        private Long miembros;
        private Long rutinas;
        private String membresiasActivas;
        private String estadoMembresia;
        private Double carga;
        private String colorCarga;

        public ComparativaEntrenadorDTO() {}

        public ComparativaEntrenadorDTO(String entrenador, Long miembros, Long rutinas, String membresiasActivas,
                                        String estadoMembresia, Double carga, String colorCarga) {
            this.entrenador = entrenador;
            this.miembros = miembros;
            this.rutinas = rutinas;
            this.membresiasActivas = membresiasActivas;
            this.estadoMembresia = estadoMembresia;
            this.carga = carga;
            this.colorCarga = colorCarga;
        }

        public String getEntrenador() {
            return entrenador;
        }

        public void setEntrenador(String entrenador) {
            this.entrenador = entrenador;
        }

        public Long getMiembros() {
            return miembros;
        }

        public void setMiembros(Long miembros) {
            this.miembros = miembros;
        }

        public Long getRutinas() {
            return rutinas;
        }

        public void setRutinas(Long rutinas) {
            this.rutinas = rutinas;
        }

        public String getMembresiasActivas() {
            return membresiasActivas;
        }

        public void setMembresiasActivas(String membresiasActivas) {
            this.membresiasActivas = membresiasActivas;
        }

        public String getEstadoMembresia() {
            return estadoMembresia;
        }

        public void setEstadoMembresia(String estadoMembresia) {
            this.estadoMembresia = estadoMembresia;
        }

        public Double getCarga() {
            return carga;
        }

        public void setCarga(Double carga) {
            this.carga = carga;
        }

        public String getColorCarga() {
            return colorCarga;
        }

        public void setColorCarga(String colorCarga) {
            this.colorCarga = colorCarga;
        }
    }
}

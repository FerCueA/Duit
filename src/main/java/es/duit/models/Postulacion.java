package es.duit.models;

import java.util.Date;

public class Postulacion {
        private Solicitud solicitud;
        public Solicitud getSolicitud() { return solicitud; }
        public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }
    private int idPostulacion;
    private int idSolicitud;
    private int idProfesional;
    private String mensaje;
    private Double precioPropuesto;
    private Date fechaPostulacion;
    private Date fechaRespuesta;
    private EstadoPostulacion estado;

    public enum EstadoPostulacion {
        PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA
    }

    public Postulacion() {}

    public int getIdPostulacion() {
        return idPostulacion;
    }

    public void setIdPostulacion(int idPostulacion) {
        this.idPostulacion = idPostulacion;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(int idProfesional) {
        this.idProfesional = idProfesional;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Double getPrecioPropuesto() {
        return precioPropuesto;
    }

    public void setPrecioPropuesto(Double precioPropuesto) {
        this.precioPropuesto = precioPropuesto;
    }

    public Date getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(Date fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public EstadoPostulacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPostulacion estado) {
        this.estado = estado;
    }
}

import com.google.gson.annotations.SerializedName;

public class Cartelera {

    // Usamos SerializedName por si el nombre en Java es distinto al de la BD, 
    // aunque aquí los mantenemos iguales por simplicidad.
    @SerializedName("id")
    private String id; // En Java, los UUID de la BD se manejan como String

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("cartel_url")
    private String cartelUrl;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getCartelUrl() { return cartelUrl; }
    public String getDescripcion() { return descripcion; }
    public String getCreatedAt() { return createdAt; }
}
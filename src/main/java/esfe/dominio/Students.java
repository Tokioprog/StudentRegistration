package esfe.dominio;

// Clase que representa a un estudiante dentro del dominio de la aplicación
public class Students {

    // Atributos privados que representan los datos de un estudiante

    private int studentId;       // Identificador único del estudiante (clave primaria)
    private String code;         // Código del estudiante (puede ser un carnet o matrícula)
    private String fullName;     // Nombre completo del estudiante
    private int age;             // Edad del estudiante
    private Career career;       // Objeto Career que representa la carrera del estudiante (relación con CareerID)

    // Constructor vacío necesario para ciertos frameworks y librerías que usan reflexión
    public Students() {
    }

    // Constructor con todos los parámetros para inicializar un objeto Students
    public Students(int studentId, String code, String fullName, int age, Career career) {
        this.studentId = studentId;
        this.code = code;
        this.fullName = fullName;
        this.age = age;
        this.career = career;
    }

    // Métodos getter y setter para acceder y modificar cada campo

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    // Método toString sobrescrito para mostrar el nombre completo y el código del estudiante
    // Esto es útil al mostrar listas en componentes gráficos como combo boxes o tablas
    @Override
    public String toString() {
        return fullName + " (" + code + ")";
    }
}

package esfe.utils;

import esfe.dominio.Career; // Necesitas importar tu clase Career aquí

public class CBOption {
    private String displayText;
    private int value; // Cambiado a int para ser consistente con careerId

    // Constructor existente (útil si quieres crear CBOptions directamente con un String y un int)
    public CBOption(String displayText, int value) { // Modificado a int
        this.displayText = displayText;
        this.value = value;
    }

    // Nuevo constructor: para cuando creas un CBOption a partir de un objeto Career
    public CBOption(Career career) {
        if (career != null) {
            this.displayText = career.getCareerName();
            this.value = career.getCareerId();
        } else {
            this.displayText = ""; // O un valor por defecto como "-- Seleccione --"
            this.value = 0; // O un valor que indique "ninguna selección"
        }
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getValue() { // Cambiado a int
        return value;
    }

    @Override
    public String toString() {
        return displayText; // Esto es lo que se mostrará en el JComboBox
    }

    @Override
    public boolean equals(Object obj) {
        // Optimizado para ser más robusto y común en Java
        if (this == obj) return true; // Si es la misma instancia
        if (obj == null || getClass() != obj.getClass()) return false; // Si es nulo o de otra clase

        CBOption other = (CBOption) obj; // Casting seguro
        return value == other.value; // Compara los valores enteros directamente
    }

    @Override
    public int hashCode() {
        // Es una buena práctica sobrescribir hashCode cuando se sobrescribe equals
        return Integer.hashCode(value);
    }
}
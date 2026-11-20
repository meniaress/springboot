package tn.esprit.autoloc.exception;

/** Identifiant inexistant : traduit en HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable (id = " + id + ")");
    }
}

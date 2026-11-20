package tn.esprit.autoloc.exception;

/** Opération refusée par une règle de gestion (doublon, lien inexistant...) : traduit en HTTP 409. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}

package automat;

import events.ErrorEvent;
import events.ErrorEventHandler;
import events.InputStringEvent;
import events.InputStringListener;
import exceptions.AlreadyExistsException;
import exceptions.EmptyListException;
import exceptions.FullAutomatException;
import exceptions.InvalidInputException;

import java.util.NoSuchElementException;

public class AutomatInputStringListener implements InputStringListener {
    private AutomatImpl automat;
    private ErrorEventHandler errorHandler;


    @Override
    public void addEvent(InputStringEvent event) {
        switch(event.getType()){
            case addHersteller:
                try {
                    this.automat.addHersteller(new HerstellerImpl(event.getMessage()));
                } catch (AlreadyExistsException e) {
                    this.errorHandler.handle(new ErrorEvent(this, "es existiert bereits ein Hersteller mit diesem Namen"));
                }
                break;
            case remHersteller:
                try {
                    this.automat.removeHersteller(event.getMessage());
                } catch (NoSuchElementException e) {
                    this.errorHandler.handle(new ErrorEvent(this, "dieser Hersteller existiert nicht"));
                }
        }
    }

    public void setAutomat(AutomatImpl automat) {
        this.automat = automat;
    }

    public void setErrorHandler(ErrorEventHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
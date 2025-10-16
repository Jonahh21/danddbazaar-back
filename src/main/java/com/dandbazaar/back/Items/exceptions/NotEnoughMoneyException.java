package com.dandbazaar.back.Items.exceptions;

public class NotEnoughMoneyException extends Exception {
    
    public NotEnoughMoneyException() {
        super("Tu grupo no tiene suficiente dinero para comprar esto");
    }
}

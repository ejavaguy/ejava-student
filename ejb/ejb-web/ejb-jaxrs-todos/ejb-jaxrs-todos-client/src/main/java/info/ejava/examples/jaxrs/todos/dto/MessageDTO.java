package info.ejava.examples.jaxrs.todos.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message", namespace="urn:ejava.jaxrs.todos")
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String text;

    public MessageDTO() {}
    public MessageDTO(String message) {
        this.text = message;
    }
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}

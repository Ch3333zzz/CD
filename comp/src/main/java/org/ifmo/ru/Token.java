package org.ifmo.ru;

public class Token {
    private TokenType tokenType;
    private String value;
    private int position;

    public Token(TokenType tokenType, String value, int position) {
        this.tokenType = tokenType;
        this.value = value;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Token(Type: {" + this.tokenType +"}, Value: '{" +  this.value + "}') at {"+ this.position + "}";
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
    public TokenType getTokenType() {
        return this.tokenType;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return this.position;
    }
}

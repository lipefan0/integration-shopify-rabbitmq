package br.com.contis.consumer.graphql;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Map;

public class GraphQLResponse<T> {
    private T data;
    private GraphQLError[] errors;

    public static class GraphQLError {
        private String message;

        @JsonAlias("extensions")
        private Map<String, Object> extensions;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getExtensions() {
            return extensions;
        }

        public void setExtensions(Map<String, Object> extensions) {
            this.extensions = extensions;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public GraphQLError[] getErrors() {
        return errors;
    }

    public void setErrors(GraphQLError[] errors) {
        this.errors = errors;
    }
}

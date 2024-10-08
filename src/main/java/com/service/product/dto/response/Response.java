/**
 * author @bhupendrasambare
 * Date   :27/06/24
 * Time   :5:01 pm
 * Project:microservices-registry
 **/
package com.service.product.dto.response;

import com.service.product.config.Constants;
import com.service.product.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private Status status = Status.SUCCESS;
    private String code = Constants.SUCCESS_CODE;
    private String message = Constants.SUCCESS;
    private Object data;

    public Response(Exception e){
        this.code = Constants.INTERNAL_SERVER_ERROR_CODE;
        this.message = e.getMessage();
        this.status = Status.FAILED;
    }

    public Response(String message){
        this.message = message;
    }

    public Response(Status status,String code,String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public Response(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}

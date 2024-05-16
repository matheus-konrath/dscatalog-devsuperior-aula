package com.devsuperior.dscatalog.services.validation;

import com.devsuperior.dscatalog.dto.UserInsertDto;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repository.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDto> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid ann){

    }

    @Override
    public boolean isValid(UserInsertDto dto, ConstraintValidatorContext context){

        List<FieldMessage> list = new ArrayList<>();

        // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

        User user = userRepository.findByEmail(dto.getEmail());
        if(user != null){
            list.add(new FieldMessage("email", "Email já existe"));
        }

        // Inserindo na lista de erros do BeansValidation o tipo de erro
        for (FieldMessage e : list){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldMessage())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}

package nikolaiev.v.o.shop.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//обработка случая когда не найдено сообщение
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundExeption extends RuntimeException {
}

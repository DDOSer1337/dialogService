package dialogService.exeptionHandler;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
public class AppError {
    private int statusCode;
    private String message;
}

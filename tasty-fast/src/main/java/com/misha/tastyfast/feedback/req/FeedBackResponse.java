package com.misha.tastyfast.feedback.req;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponse {

    private Double note;
    private String comment;
    private boolean ownFeedback;




}

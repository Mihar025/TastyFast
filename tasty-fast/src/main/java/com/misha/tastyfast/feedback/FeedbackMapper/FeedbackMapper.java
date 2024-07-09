package com.misha.tastyfast.feedback.FeedbackMapper;

import com.misha.tastyfast.feedback.req.FeedBackRequest;
import com.misha.tastyfast.feedback.req.FeedBackResponse;
import com.misha.tastyfast.model.Feedback;
import com.misha.tastyfast.model.Product;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedBackRequest request) {

        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .product(Product.builder()
                        .id(request.bookId())
                        .inStock(false)
                        .build()
                )

                .build();
    }

    public FeedBackResponse toFeedBackResponse(Feedback feedback, Integer id) {
        return FeedBackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}

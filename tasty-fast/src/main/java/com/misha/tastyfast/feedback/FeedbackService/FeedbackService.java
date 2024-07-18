package com.misha.tastyfast.feedback.FeedbackService;

import com.misha.tastyfast.exception.OperationNotPermittedException;
import com.misha.tastyfast.feedback.FeedbackMapper.FeedbackMapper;
import com.misha.tastyfast.feedback.feedbackrepository.FeedbackRepository;
import com.misha.tastyfast.feedback.req.FeedBackRequest;
import com.misha.tastyfast.feedback.req.FeedBackResponse;
import com.misha.tastyfast.model.Feedback;
import com.misha.tastyfast.model.Product;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.repositories.ProductRepository;
import com.misha.tastyfast.requests.pageResponse.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final ProductRepository productRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedBackRequest request, Authentication connectedUser) {
        Product product = productRepository.findById(request.bookId()).orElseThrow(
                () -> new EntityNotFoundException("No book with the ID::" + request.bookId())
        );
        if(!product.isInStock()){
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not shareable book");
        }
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(product.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot give a feedback to your own book");
        }
        Feedback feedBack = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedBack).getId();
    }

    public PageResponse<FeedBackResponse> findAllFeedBacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page,size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllByProductId(bookId, pageable);
        List<FeedBackResponse> feedBackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedBackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedBackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }



}

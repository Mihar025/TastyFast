package com.misha.tastyfast.feedback.FeedbackController;

import com.misha.tastyfast.feedback.FeedbackService.FeedbackService;
import com.misha.tastyfast.feedback.req.FeedBackRequest;
import com.misha.tastyfast.feedback.req.FeedBackResponse;
import com.misha.tastyfast.requests.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedBack(
            @Valid @RequestBody FeedBackRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedBackResponse>> findAllFeedBacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllFeedBacksByBook(bookId, page, size, connectedUser));
    }



}

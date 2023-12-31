package com.example.restaurantestimate.services;

import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.ReviewDtoRequest;
import com.example.restaurantestimate.dto.ReviewDtoUpdateRequest;
import com.example.restaurantestimate.dto.restaurant.RestaurantCard;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.dto.review.ReviewPages;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.entities.Review;
import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.exceptions.CustomAccessDeniedException;
import com.example.restaurantestimate.exceptions.EntityAlreadyExistException;
import com.example.restaurantestimate.mappers.RestaurantMapper;
import com.example.restaurantestimate.mappers.ReviewSerializer;
import com.example.restaurantestimate.repositories.RestaurantRepository;
import com.example.restaurantestimate.repositories.ReviewRepository;
import com.example.restaurantestimate.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    private final RestaurantRepository restaurantRepository;
    private final ReviewSerializer reviewSerializer;

    public User findUser() {
        return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    }
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден!"));
    }
    @Transactional
    public ReviewDto createReview(ReviewDtoRequest reviewDtoRequest) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(user.getUsername());
        if (reviewRepository.findAllByUserIdAndRestaurantId(user.getId(), reviewDtoRequest.getRestaurantId()).isPresent()) {
            throw new EntityAlreadyExistException("Review on this restaurant is already created");
        }

        Restaurant restaurant = restaurantRepository.findById(reviewDtoRequest.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Restaurant with such id '%s' is not found", reviewDtoRequest.getRestaurantId()))
                );

        Review review = Review.builder()
                .rating(reviewDtoRequest.getRating())
                .userReview(reviewDtoRequest.getReview())
                .restaurant(restaurant)
                .user(user)
                .build();

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }
    @Transactional
    public ReviewPages getAllReviews(Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAll(pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewDto getReviewByUserIdAndMovieId(Long userId, Long movieId) {
        Review review = reviewRepository.findAllByUserIdAndRestaurantId(userId, movieId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Ревью на фильм с id '%s' от пользователя с айди '%s' не найдено", movieId, userId)
                ));

        return reviewSerializer.apply(review);
    }

    @Transactional
    public ReviewPages getReviewByRestaurantId(Long restaurantId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByRestaurantId(restaurantId, pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewPages getReviewByUserId(Long userId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByUserIdOrderByIdDesc(userId, pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewDtoUpdateRequest reviewDtoRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Ревью с данным id '%s' не найдено", reviewId)));

        review.setId(reviewId);
        review.setUserReview(reviewDtoRequest.getReview());
        review.setRating(reviewDtoRequest.getRating());

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }

    @Transactional
    public ResponseMessage deleteReview(Long reviewId) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Ревью с данным ID '%s' найдено", reviewId)
                ));
        if (!Objects.equals(review.getUser().getId(), user.getId())) {
            throw new CustomAccessDeniedException("Вы не можете удалить ревью другого пользователя.");
        }
        reviewRepository.deleteById(reviewId);
        return new ResponseMessage(200, "Ревью успешно удалено.", Date.from(Instant.now()).toString());
    }

    @Transactional
    public Double getAverageReviewRatingById(Long id) {
        Integer count = reviewRepository.countByRestaurantId(id);
        Integer countReviewToCalcAverage = 5;
        if (count > 0 && count % countReviewToCalcAverage == 0) {
            return getAverage(id);
        }
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        return restaurant.map(Restaurant::getRating).orElse(null);
    }

    @Transactional
    public Double getAverage(Long id) {
        Double avg = reviewRepository.getAvgRatingByRestaurantId(id);
        Restaurant movie = restaurantRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Фильм с данным ID не найден!"));
        movie.setId(id);
        movie.setRating(Math.round(avg * 10.0) / 10.0);
        restaurantRepository.save(movie);
        return Math.round(avg * 10.0) / 10.0;
    }

    public List<ReviewDto> getRandomReviewsByRestaurantName(String restaurantName) {
        return reviewRepository.findRandomReviewByRestaurantName(restaurantName).stream()
                .map(reviewSerializer)
                .toList();
    }


}
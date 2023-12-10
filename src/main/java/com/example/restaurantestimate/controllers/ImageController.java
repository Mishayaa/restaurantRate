package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.ImageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageUtils imageUtils;
    public static final String IMAGE_CONTROLLER_PATH = "/api/image";

    @GetMapping(value = IMAGE_CONTROLLER_PATH + "/{imageName}")
    public ResponseEntity<byte[]> testGetImage(@PathVariable(value = "imageName")
                                               @Parameter(description = "Ссылка на аватар пользователя",
                                                       example = "{username}_avatar.ext")
                                               String imageName) throws IOException {
        File image = imageUtils.getImage(imageName);
        byte[] bytes = StreamUtils.copyToByteArray(image.toURI().toURL().openStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}
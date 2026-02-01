package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.label.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Labels",
        description = "Endpoints for managing labels")
@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @Operation(
            summary = "Create a new label",
            description = "Creates a new label with the provided details. "
            + "The authenticated user will be associated with the label as its creator.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Label created successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto create(
            @RequestBody @Valid CreateLabelRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return labelService.create(requestDto, user);
    }

    @Operation(
            summary = "Get labels for the authenticated user",
            description = "Returns a paginated list of labels "
            + "created by the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Labels retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    )
            }
    )
    @GetMapping
    public Page<LabelDto> getLabels(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return labelService.getLabels(user.getId(), pageable);
    }

    @Operation(
            summary = "Update a label",
            description = "Updates an existing label. Only the user who created "
            + "the label is permitted to modify it.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Label updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the creator of the label may update it"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Label not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public LabelDto update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @RequestBody @Valid UpdateLabelRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return labelService.update(id, requestDto, user.getId());
    }

    @Operation(
            summary = "Delete a label",
            description = "Deletes a label. Only the user who created "
            + "the label is permitted to delete it.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Label deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the creator of the label may delete it"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Label not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @AuthenticationPrincipal User user
    ) {
        labelService.delete(id, user.getId());
    }
}

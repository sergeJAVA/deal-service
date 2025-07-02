package com.example.deal_service.controller;

import com.example.deal_service.model.DealContractorRequest;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.service.DealContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Контроллер для управления контрагентами в сделках.
 */
@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
@Tag(name = "Управление контрагентами сделки",
        description = "API для добавления, обновления и удаления контрагентов из сделки")
public class DealContractorController {

    private final DealContractorService dealContractorService;

    /**
     * Сохраняет (создает или обновляет) информацию о контрагенте в сделке.
     *
     * @param request Запрос с данными контрагента для сохранения.
     * @return DTO сохраненного контрагента.
     */
    @PutMapping("/save")
    @Operation(summary = "Сохранить контрагента в сделке", description = "Создает нового или обновляет существующего контрагента в рамках сделки." +
            " Указывать поле \"id\" в JSON только в том случае," +
            " если не собираетесь создавать нового контрагента, а хотите изменить существующего.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контрагент успешно сохранен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DealContractorDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "404", description = "Сделка или контрагент не найдены по предоставленному id", content = @Content)
    })
    public ResponseEntity<DealContractorDto> saveDealContractor(@RequestBody DealContractorRequest request) {
        DealContractorDto savedDto = dealContractorService.saveDealContractor(request);
        return ResponseEntity.ok(savedDto);
    }

    /**
     * Удаляет контрагента из сделки по его ID.
     *
     * @param id Уникальный идентификатор контрагента в сделке (DealContractor ID).
     * @return Ответ без содержимого (204 No Content).
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Удалить контрагента из сделки", description = "Удаляет контрагента из сделки по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Контрагент успешно удален", content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "404", description = "Контрагент не найден", content = @Content)
    })
    public ResponseEntity<Void> deleteDealContractor(@RequestParam UUID id) {
        dealContractorService.deleteDealContractor(id);
        return ResponseEntity.noContent().build();
    }

}

package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.PdfReportMapper;
import com.Logisight.repository.PdfReportRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.PdfReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfReportManager implements PdfReportService {

    private final PdfReportRepository reportRepo;
    private final UserRepository userRepo;
    private final PdfReportMapper mapper;

    @Override
    public PdfReportResponseDTO createPdfReport(CreatePdfReportDTO createDTO) {
        User user = userRepo.findById(createDTO.getGeneratedByUserId())
            .orElseThrow(() -> new BusinessException(
                ErrorCode.PDF_REPORT_ASSOCIATED_USER_NOT_FOUND));
        PdfReport entity = mapper.toEntity(createDTO);
        entity.setGeneratedBy(user);

        PdfReport saved;
        try {
            saved = reportRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_CREATION_FAILED, ex.getMessage());
        }
        return mapper.toResponseDto(saved);
    }

    @Override
    public Optional<PdfReportResponseDTO> updatePdfReport(Long id, UpdatePdfReportDTO updateDTO) {
        PdfReport entity = reportRepo.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.PDF_REPORT_NOT_FOUND));
        mapper.updateEntity(updateDTO, entity);

        PdfReport updated;
        try {
            updated = reportRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_UPDATE_FAILED, ex.getMessage());
        }
        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    public Optional<PdfReportResponseDTO> getPdfReportById(Long id) {
        return reportRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    public List<PdfReportResponseDTO> getPdfReportsByUserId(Long userId) {
        return reportRepo.findByGeneratedById(userId)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PdfReportResponseDTO> searchPdfReportsByName(String reportName) {
        return reportRepo.findByReportNameContainingIgnoreCase(reportName)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PdfReportResponseDTO> getPdfReportsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_INVALID_DATE_RANGE);
        }
        return reportRepo.findByGeneratedAtBetween(start, end)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deletePdfReport(Long id) {
        if (!reportRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.PDF_REPORT_NOT_FOUND);
        }
        try {
            reportRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_DELETION_FAILED, ex.getMessage());
        }
    }
}

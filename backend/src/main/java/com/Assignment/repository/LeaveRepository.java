package com.Assignment.repository;

import com.Assignment.entity.Leave;
import com.Assignment.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    List<Leave> findByEmployeeId(Long employeeId);
    
    List<Leave> findByStatus(LeaveStatus status);
    
    List<Leave> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
    
    List<Leave> findByLeaveType(com.Assignment.entity.LeaveType leaveType);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
           "AND l.startDate >= :startDate AND l.endDate <= :endDate")
    List<Leave> findByEmployeeIdAndDateRange(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT l FROM Leave l WHERE l.startDate >= :startDate AND l.endDate <= :endDate")
    List<Leave> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    //  List<Leave> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
    //     LocalDate startDate,
    //     LocalDate endDate
    // );
    
    List<Leave> findByEmployeeDepartmentAndStatus(String department, LeaveStatus status);
    
    Long countByStatus(LeaveStatus status);
}

package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.QueueStatusResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.WashBay;
import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.WashBayStatus;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.WashBayRepository;
import com.carwash.carwashsystem.repository.WashQueueRepository;
import com.carwash.carwashsystem.service.interfaces.AssignmentService;
import com.carwash.carwashsystem.service.interfaces.QueueService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {


    private final WashQueueRepository washQueueRepository;
    private final BookingRepository bookingRepository;
    private final WashBayRepository washBayRepository;
    private final AssignmentService assignmentService;




    @Override
    @Transactional
    public void addToQueue(Long bookingId) {


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + bookingId));


        // tránh duplicate queue
        if(washQueueRepository.findByBookingId(bookingId).isPresent()){
            return;
        }


        Integer position =
                washQueueRepository.findTodayActiveQueue().size() + 1;



        WashQueue queue = WashQueue.builder()
                .booking(booking)
                .queuePosition(position)
                .enqueuedAt(LocalDateTime.now())
                .status(BookingStatus.WAITING)
                .build();



        washQueueRepository.save(queue);


        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

    }



    @Override
    @Transactional
    public void removeFromQueue(Long bookingId) {

        WashQueue queue =
                washQueueRepository.findByBookingId(bookingId)
                        .orElseThrow();

        washQueueRepository.delete(queue);

        updateQueuePositions();

    }



    @Override
    public void advanceQueue(Long bookingId){

        WashQueue queue =
                washQueueRepository.findByBookingId(bookingId)
                        .orElseThrow();


        queue.setStatus(BookingStatus.WASHING);

        queue.setStartedAt(LocalDateTime.now());


        washQueueRepository.save(queue);



        Booking booking = queue.getBooking();

        booking.setStatus(BookingStatus.WASHING);

        bookingRepository.save(booking);

    }



    @Override
    @Transactional
    public void moveToNextInQueue(Long bayId) {

        WashQueue nextQueue =
                washQueueRepository
                        .findByStatusOrderByEnqueuedAtAsc(
                                BookingStatus.WAITING)
                        .stream()
                        .findFirst()
                        .orElse(null);

        if(nextQueue == null){
            return;
        }

        Booking booking = nextQueue.getBooking();

        WashBay bay =
                washBayRepository.findById(bayId)
                        .orElseThrow();
        bay.setStatus(WashBayStatus.OCCUPIED);

        washBayRepository.save(bay);

        booking.setAssignedBay(bay);

        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        assignmentService.autoAssignWasher(
                booking.getId()
        );

        nextQueue.setStatus(BookingStatus.WAITING);

        nextQueue.setStartedAt(LocalDateTime.now());

        washQueueRepository.save(nextQueue);


    }

    @Override
    public List<QueueStatusResponse> getCurrentQueue() {

        return washQueueRepository
                .findTodayActiveQueue()
                .stream()
                .map(queue -> QueueStatusResponse.builder()
                        .bookingId(queue.getBooking().getId())
                        .customerName(queue.getBooking().getCustomer().getFullName())
                        .licensePlate(queue.getBooking().getVehicle().getLicensePlate())
                        .queuePosition(queue.getQueuePosition())
                        .status(queue.getStatus())
                        .enqueuedAt(queue.getEnqueuedAt())
                        .build())
                .toList();
    }


    @Override
    public QueueStatusResponse getQueuePosition(Long bookingId) {

        WashQueue queue =
                washQueueRepository.findByBookingId(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found in queue: " + bookingId
                                ));


        return QueueStatusResponse.builder()
                .bookingId(queue.getBooking().getId())
                .customerName(queue.getBooking().getCustomer().getFullName())
                .licensePlate(queue.getBooking().getVehicle().getLicensePlate())
                .queuePosition(queue.getQueuePosition())
                .status(queue.getStatus())
                .enqueuedAt(queue.getEnqueuedAt())
                .build();
    }
    private void updateQueuePositions() {

        List<WashQueue> queues =
                washQueueRepository.findByStatusOrderByEnqueuedAtAsc(
                        BookingStatus.WAITING
                );

        int position = 1;

        for (WashQueue queue : queues) {

            queue.setQueuePosition(position++);

        }

        washQueueRepository.saveAll(queues);

    }
}
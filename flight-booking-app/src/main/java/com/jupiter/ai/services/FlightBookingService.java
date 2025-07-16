//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jupiter.ai.services;

import com.jupiter.ai.data.Booking;
import com.jupiter.ai.data.BookingClass;
import com.jupiter.ai.data.BookingData;
import com.jupiter.ai.data.BookingStatus;
import com.jupiter.ai.data.Customer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class FlightBookingService {
	private final BookingData db = new BookingData();

	public FlightBookingService() {
		this.initDemoData();
	}

	private void initDemoData() {
		List<String> names = List.of("Jupiter", "徐庶", "诸葛", "百里", "楼兰", "庄周");
		List<String> airportCodes = List.of("Canton", "HongKong", "London", "New York", "北京", "上海", "广州", "深圳", "杭州", "南京", "青岛", "成都", "武汉", "西安", "重庆", "大连", "天津");
		Random random = new Random();
		ArrayList<Customer> customers = new ArrayList();
		ArrayList<Booking> bookings = new ArrayList();

		for(int i = 0; i < 5; ++i) {
			String name = (String)names.get(i);
			String from = (String)airportCodes.get(random.nextInt(airportCodes.size()));
			String to = (String)airportCodes.get(random.nextInt(airportCodes.size()));
			BookingClass bookingClass = BookingClass.values()[random.nextInt(BookingClass.values().length)];
			Customer customer = new Customer();
			customer.setName(name);
			LocalDate date = LocalDate.now().plusDays((long)(2 * (i + 1)));
			Booking booking = new Booking("10" + (i + 1), date, customer, BookingStatus.CONFIRMED, from, to, bookingClass);
			customer.getBookings().add(booking);
			customers.add(customer);
			bookings.add(booking);
		}

		this.db.setCustomers(customers);
		this.db.setBookings(bookings);
	}

	public List<BookingTools.BookingDetails> getBookings() {
		return this.db.getBookings().stream().map(this::toBookingDetails).toList();
	}

	private Booking findBooking(String bookingNumber, String name) {
		return (Booking)this.db.getBookings().stream().filter((b) -> b.getBookingNumber().equalsIgnoreCase(bookingNumber)).filter((b) -> b.getCustomer().getName().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Booking not found"));
	}

	public BookingTools.BookingDetails getBookingDetails(String bookingNumber, String name) {
		Booking booking = this.findBooking(bookingNumber, name);
		return this.toBookingDetails(booking);
	}

	public void changeBooking(String bookingNumber, String name, String newDate, String from, String to) {
		Booking booking = this.findBooking(bookingNumber, name);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(1L))) {
			throw new IllegalArgumentException("Booking cannot be changed within 24 hours of the start date.");
		} else {
			booking.setDate(LocalDate.parse(newDate));
			booking.setFrom(from);
			booking.setTo(to);
		}
	}

	public void cancelBooking(String bookingNumber, String name) {
		Booking booking = this.findBooking(bookingNumber, name);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(2L))) {
			throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
		} else {
			booking.setBookingStatus(BookingStatus.CANCELLED);
		}
	}

	private BookingTools.BookingDetails toBookingDetails(Booking booking) {
		return new BookingTools.BookingDetails(booking.getBookingNumber(), booking.getCustomer().getName(), booking.getDate(), booking.getBookingStatus(), booking.getFrom(), booking.getTo(), booking.getBookingClass().toString());
	}
}

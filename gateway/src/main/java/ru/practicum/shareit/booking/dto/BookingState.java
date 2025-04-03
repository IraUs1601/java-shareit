package ru.practicum.shareit.booking.dto;

import java.util.Optional;

/**
 * Перечисление состояний бронирования.
 * Используется для фильтрации и отображения бронирований по их статусу.
 */
public enum BookingState {

	/** Все бронирования, без фильтрации по статусу. */
	ALL,

	/** Текущие бронирования (в процессе исполнения). */
	CURRENT,

	/** Будущие бронирования (еще не начались). */
	FUTURE,

	/** Завершенные бронирования (уже окончены). */
	PAST,

	/** Отклоненные бронирования. */
	REJECTED,

	/** Бронирования, ожидающие подтверждения. */
	WAITING;

	/**
	 * Получает значение {@link BookingState} из строки, игнорируя регистр.
	 *
	 * @param stringState строковое представление состояния
	 * @return {@link Optional} с соответствующим {@link BookingState}, если найдено; иначе пустой Optional
	 */
	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
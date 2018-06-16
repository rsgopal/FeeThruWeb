package com.janakan.feethru.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedItem<T> {
	private boolean isSelected;
	private T item;
}

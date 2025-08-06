package br.com.projeto.spring.domain.dto.request;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EntidadeIdRequest(

		@NotNull
		@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
				message = ValidationMessagesKeys.GENERICO_UUID_PATTERN)
		String id) {

}

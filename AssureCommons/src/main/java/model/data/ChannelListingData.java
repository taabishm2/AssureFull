package model.data;

import lombok.Getter;
import lombok.Setter;
import model.form.ChannelListingForm;

@Getter
@Setter
public class ChannelListingData extends ChannelListingForm {
    private Long id;

    private String channelName;

    private String clientName;
}

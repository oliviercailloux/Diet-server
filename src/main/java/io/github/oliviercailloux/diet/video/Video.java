package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "fileId", "url", "description", "side" })
public class Video {
	static Video fromPersistent(VideoEntity video) {
		return new Video(video);
	}

	private final VideoEntity video;

	protected Video(VideoEntity video) {
		this.video = checkNotNull(video);
		checkArgument(video.isPersistent());
	}

	VideoEntity video() {
		return video;
	}

	public int getFileId() {
		return video.getFileId();
	}

	public URI getUrl() {
		return video.getUrl();
	}

	public String getDescription() {
		return video.getDescription();
	}

	public Side getSide() {
		return video.getSide();
	}

}

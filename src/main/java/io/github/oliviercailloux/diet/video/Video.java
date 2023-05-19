package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.net.URI;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Video)) {
			return false;
		}
		final Video t2 = (Video) o2;
		return getFileId() == t2.getFileId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFileId());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("file id", getFileId()).toString();
	}
}

package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.utils.Utils;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@NamedQuery(name = "latest file id", query = "SELECT MAX(v.fileId) FROM Video v")
@NamedQuery(name = "replies", query = "SELECT a.video FROM Video v, ArguerAttack a JOIN a.counters v WHERE v IN (:videos)")
@NamedQuery(name = "starters", query = "SELECT v FROM Video v WHERE v.counters IS EMPTY")
@NamedQuery(name = "get", query = "SELECT v FROM Video v WHERE v.fileId = :fileId")
@NamedQuery(name = "all", query = "SELECT v FROM Video v LEFT JOIN FETCH v.counters LEFT JOIN FETCH v.counteredBy ORDER BY v.fileId")
public class VideoEntity {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoEntity.class);

	private static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.ENGLISH);

	static {
		FORMATTER.setMinimumIntegerDigits(3);
		FORMATTER.setMaximumIntegerDigits(3);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonbTransient
	private int id;

	@NotNull
	@Column(unique = true)
	private int fileId;

	@NotNull
	@Column(unique = true)
	private String description;

	@OneToMany(mappedBy = "video")
	@JsonbTransient
	private Set<ArguerAttack> counters;

	@OneToMany(mappedBy = "counters")
	@JsonbTransient
	private Set<ArguerAttack> counteredBy;

	private Side side;

	VideoEntity() {
		id = -1;
		counters = null;
		counteredBy = null;
	}

	public VideoEntity(int fileId, String description, Side side) {
		this();
		this.fileId = fileId;
		this.description = checkNotNull(description);
		this.side = checkNotNull(side);
	}

	boolean isPersistent() {
		return id != -1;
	}

	public int getFileId() {
		return fileId;
	}

	public URI getUrl() {
		return Utils.https("www.lamsade.dauphine.fr", "/~ocailloux/Diet/" + FORMATTER.format(fileId) + ".mp4");
	}

	public String getDescription() {
		return description;
	}

	public Side getSide() {
		return side;
	}

	boolean hasCounters() {
		final boolean hasCounters = counters != null;
		final boolean hasCounteredBy = counteredBy != null;
		verify(hasCounters == hasCounteredBy);
		return hasCounters;
	}

	Set<ArguerAttack> counters() {
		checkState(hasCounters());
		return counters;
	}

	Set<ArguerAttack> counteredBy() {
		checkState(hasCounters());
		return counteredBy;
	}

	public ImmutableSet<VideoEntity> getCountersVideos() {
		return counters().stream().map(ArguerAttack::getCounters).collect(ImmutableSet.toImmutableSet());
	}

	public ImmutableSet<Integer> getCountersFileIds() {
		return getCountersVideos().stream().map(VideoEntity::getFileId).sorted().collect(ImmutableSet.toImmutableSet());
	}

	public ImmutableSet<VideoEntity> getCounteredBy() {
		checkState(counters != null);
		return counters.stream().map(ArguerAttack::getVideo).collect(ImmutableSet.toImmutableSet());
	}

	public void addCounters(ArguerAttack attack) {
		checkArgument(attack.getVideo().equals(this));
		counters.add(attack);
	}

	public ArguerAttack addCounters(VideoEntity countered) {
		final ArguerAttack attack = new ArguerAttack(this, countered);
		counters.add(attack);
		countered.addCounteredBy(attack);
		return attack;
	}

	public void addCounteredBy(ArguerAttack attack) {
		checkArgument(attack.getCounters().equals(this));
		counteredBy.add(attack);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("fileId", fileId).add("description", description)
				.add("counters", counters).add("counteredBy", counteredBy).toString();
	}
}

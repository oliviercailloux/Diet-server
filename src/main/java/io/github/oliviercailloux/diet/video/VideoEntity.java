package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;

import com.google.common.base.MoreObjects;
import io.github.oliviercailloux.diet.utils.Utils;
import java.net.URI;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
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
import javax.persistence.Persistence;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@NamedQuery(name = "latest file id", query = "SELECT MAX(v.fileId) FROM VideoEntity v")
@NamedQuery(name = "replies", query = "SELECT a.video FROM ArguerAttack a JOIN a.counters v WHERE v IN (:videos)")
@NamedQuery(name = "starters", query = "SELECT v FROM VideoEntity v WHERE v.counters IS EMPTY")
@NamedQuery(name = "get", query = "SELECT v FROM VideoEntity v WHERE v.fileId = :fileId")
@NamedQuery(name = "getWithCounters", query = "SELECT v FROM VideoEntity v LEFT JOIN FETCH v.counters LEFT JOIN FETCH v.counteredBy WHERE v.fileId = :fileId")
@NamedQuery(name = "all", query = "SELECT v FROM VideoEntity v LEFT JOIN FETCH v.counters LEFT JOIN FETCH v.counteredBy ORDER BY v.fileId")
class VideoEntity {
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

	@NotNull
	private Side side;

	VideoEntity() {
		id = -1;
		counters = null;
		counteredBy = null;
	}

	VideoEntity(int fileId, String description, Side side) {
		this();
		this.fileId = fileId;
		this.description = checkNotNull(description);
		this.side = checkNotNull(side);
		counters = new LinkedHashSet<>();
		counteredBy = new LinkedHashSet<>();
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
		final boolean hasCounters = Persistence.getPersistenceUtil().isLoaded(this, "counters");
		final boolean hasCounteredBy = Persistence.getPersistenceUtil().isLoaded(this, "counteredBy");
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("fileId", fileId).add("description", description)
				.add("counters", counters).add("counteredBy", counteredBy).toString();
	}
}

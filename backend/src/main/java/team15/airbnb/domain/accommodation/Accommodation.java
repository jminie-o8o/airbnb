package team15.airbnb.domain.accommodation;

import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import team15.airbnb.domain.BaseEntity;
import team15.airbnb.domain.Reservation;
import team15.airbnb.domain.Review;
import team15.airbnb.domain.User;

@Entity
public class Accommodation extends BaseEntity {

	@Id
	@Column(name = "accommodation_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String accommodationName;

	private String description;

	@Enumerated(value = EnumType.STRING)
	private AccommodationType accommodationType;

	@NotNull
	private int price;

	private String mainImage;

	@Embedded
	private Vat vat;

	@Embedded
	private AccommodationDetails accommodationDetails;

	@NotNull
	private LocalDateTime checkInTime;

	@NotNull
	private LocalDateTime checkOutTime;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User host;

	@NotNull
	@OneToMany(mappedBy = "accommodation")
	private List<Review> reviews = new ArrayList<>();

	@NotNull
	@OneToMany(mappedBy = "accommodation")
	private List<Reservation> reservations = new ArrayList<>();

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

	@NotNull
	@OneToMany(mappedBy = "accommodation")
	private List<AccommodationImage> images = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "policy_id")
	private DiscountPolicy discountPolicy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private Event event;

	@OneToOne
	@JoinColumn(name = "region_id")
	private Region region;
}

package io.github.oliviercailloux.diet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.ReadEvent;
import io.github.oliviercailloux.diet.user.ReadEventAccepted;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserStatus;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.Side;
import io.github.oliviercailloux.diet.video.Video;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserStatusJsonTests {

	@Inject
	Jsonb jsonb;
	@Inject
	UserFactory userFactory;

	@Test
	void testWithEvents() {
		final Instant e1 = Instant.parse("2000-01-20T10:10:10.000000000Z");
		final Instant e2 = e1.plusSeconds(1);
		final Instant e3 = e2.plusSeconds(1);
		final ImmutableSet<ReadEvent> events = ImmutableSet.of(ReadEventAccepted.at(e1),
				ReadEventJudgment.at(e2, new Judgment(3, 0)),
				ReadEventSeen.at(e3, new Video(6, "Effort écologique", Side.VEGAN)));
		final UserStatus statusObj = userFactory.fictitious("u", events);
		final String statusJson = jsonb.toJson(statusObj);
		final String expected = """

				{
				    "username": "u",
				    "events": [
				        {
				            "type": "Accepted",
				            "creation": "%s"
				        },
				        {
				            "type": "Judgment",
				            "creation": "%s",
				            "judgment": {
				                "daysMeat": 0,
				                "daysVegan": 3
				            }
				        },
				        {
				            "type": "Seen",
				            "creation": "%s",
				            "fileId": 6
				        }
				    ],
				    "seen": [
				        {
				            "fileId": 6,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/006.mp4",
				            "description": "Effort écologique",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        }
				    ],
				    "toSee": [
				        {
				            "fileId": 1,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/001.mp4",
				            "description": "Climat et biodiversité",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 2,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/002.mp4",
				            "description": "Santé vegan",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 3,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/003.mp4",
				            "description": "Réduction pour écologie",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 4,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/004.mp4",
				            "description": "Entente",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 5,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/005.mp4",
				            "description": "Stratégie",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 7,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/007.mp4",
				            "description": "Consolidation",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 8,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/008.mp4",
				            "description": "Durable, éthique et gout",
				            "side": "VEGAN",
				            "countersFileIds": [
				            ]
				        },
				        {
				            "fileId": 9,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/009.mp4",
				            "description": "Élevage moindre mal que transport",
				            "side": "MEAT",
				            "countersFileIds": [
				                1,
				                3
				            ]
				        },
				        {
				            "fileId": 10,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/010.mp4",
				            "description": "Élevage encourage biodiversité",
				            "side": "MEAT",
				            "countersFileIds": [
				                1
				            ]
				        },
				        {
				            "fileId": 11,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/011.mp4",
				            "description": "Prairies bonnes pour GES",
				            "side": "MEAT",
				            "countersFileIds": [
				                1
				            ]
				        },
				        {
				            "fileId": 12,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/012.mp4",
				            "description": "Santé viande",
				            "side": "MEAT",
				            "countersFileIds": [
				                2
				            ]
				        },
				        {
				            "fileId": 13,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/013.mp4",
				            "description": "Viande pour ados",
				            "side": "MEAT",
				            "countersFileIds": [
				                2
				            ]
				        },
				        {
				            "fileId": 14,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/014.mp4",
				            "description": "Liberté de choix aux enfants",
				            "side": "MEAT",
				            "countersFileIds": [
				                2
				            ]
				        },
				        {
				            "fileId": 15,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/015.mp4",
				            "description": "B12 ou mauvais traitement",
				            "side": "MEAT",
				            "countersFileIds": [
				                2
				            ]
				        },
				        {
				            "fileId": 16,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/016.mp4",
				            "description": "Imposition de classe",
				            "side": "MEAT",
				            "countersFileIds": [
				            ]
				        }
				    ]
				}""";
		assertEquals(expected.formatted(e1, e2, e3), statusJson);
	}

}

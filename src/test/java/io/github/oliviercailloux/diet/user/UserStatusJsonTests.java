package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserStatusJsonTests {

	@Inject
	Jsonb jsonb;
	@Inject
	VideoFactory videoFactory;
	@Inject
	UserFactory userFactory;

	@Test
	@Transactional
	void testWithEvents() {
		final String username = "testWithEvents " + Instant.now().toString().replace(":", "");
		final Instant e1 = Instant.parse("2000-01-20T10:10:10.000000000Z");
		final Instant e2 = e1.plusSeconds(1);
		final Instant e3 = e2.plusSeconds(1);
		final UserWithEvents user;
		{
			user = userFactory.addUser(new Login(username, "user"), e1);
			final Judgment judgment = new Judgment(3, 0);
			user.persistEvent(ReadEventJudgment.at(e2, judgment));
			user.persistEvent(ReadEventSeen.at(e3, videoFactory.getVideo(6)));
		}
		final String statusJson = jsonb.toJson(user);
		final String expected = """

				{
				    "username": "%s",
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
				            "side": "VEGAN"
				        }
				    ],
				    "toSee": [
				        {
				            "fileId": 1,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/001.mp4",
				            "description": "Climat et biodiversité",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 2,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/002.mp4",
				            "description": "Santé vegan",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 3,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/003.mp4",
				            "description": "Réduction pour écologie",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 4,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/004.mp4",
				            "description": "Entente",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 5,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/005.mp4",
				            "description": "Stratégie",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 7,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/007.mp4",
				            "description": "Consolidation",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 8,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/008.mp4",
				            "description": "Durable, éthique et gout",
				            "side": "VEGAN"
				        },
				        {
				            "fileId": 9,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/009.mp4",
				            "description": "Élevage moindre mal que transport",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 10,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/010.mp4",
				            "description": "Élevage encourage biodiversité",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 11,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/011.mp4",
				            "description": "Prairies bonnes pour GES",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 12,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/012.mp4",
				            "description": "Santé viande",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 13,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/013.mp4",
				            "description": "Viande pour ados",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 14,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/014.mp4",
				            "description": "Liberté de choix aux enfants",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 15,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/015.mp4",
				            "description": "B12 ou mauvais traitement",
				            "side": "MEAT"
				        },
				        {
				            "fileId": 16,
				            "url": "https://www.lamsade.dauphine.fr/~ocailloux/Diet/016.mp4",
				            "description": "Imposition de classe",
				            "side": "MEAT"
				        }
				    ]
				}""";
		assertEquals(expected.formatted(username, e1, e2, e3), statusJson);
	}

}

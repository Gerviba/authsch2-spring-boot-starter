package hu.gerviba.authsch2springbootstarter.struct;

import hu.gerviba.authsch2springbootstarter.struct.ProfileDataResponse.ProfileDataResponseBuilder;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Access Scope
 * https://git.sch.bme.hu/kszk/authsch/wikis/api
 */
public enum Scope {
    /**
     * AuthSCH-s azonosító (varchar, maximum 24 karakter). 
     * Belépéskor a kiadásához nem szükséges a felhasználó jóváhagyása.
     */
    BASIC("basic") {
        @Override
        public boolean canApply(Map<String, Object> map) {
            return true;
        }
        
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.internalId(UUID.fromString((String) map.get("internal_id")));
        }
    },
    /**
     * Név
     */
    DISPLAY_NAME("displayName") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.displayName((String) map.get(getScope()));
        }
    },
    /**
     * Vezetéknév
     */
    SURNAME("sn") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.surname((String) map.get(getScope()));
        }
    },
    /**
     * Keresztnév
     */
    GIVEN_NAME("givenName") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.givenName((String) map.get(getScope()));
        }
    },
    /**
     * E-mail cím
     */
    EMAIL("mail") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.mail((String) map.get(getScope()));
        }
    },
    /**
     * Neptun kód (csak abban az esetben, ha a felhasználónak be van kötve a BME címtár 
     * azonosítója is, egyébként null-t ad vissza). Fokozottan védett információ, 
     * ami azt jelenti, hogy alapból nem kérhető le (invalid scope hibával kerül 
     * visszatérésre az ezt tartalmazó engedélykérés), csak indokolt esetben, központi 
     * engedélyezés után használható (ehhez adj fel egy ticketet a support.sch.bme.hu 
     * oldalon, amelyben leírod hogy mihez és miért van rá szükséged.
     * <br>
     * <b>warning</b> Külön engedélyeztetni kell.
     */
    NEPTUN_CODE("niifPersonOrgID") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.neptun((String) map.get(getScope()));
        }
    },
    /**
     * Kapcsolt accountok, kulcs - érték párokban. Lehetséges kulcsok:
     * <li> bme: szám@bme.hu </li>
     * <li> schacc: schacc username </li>
     * <li> vir: vir id (integer) </li>
     * <li> virUid: vir username </li>
     */
    LINKED_ACCOUNTS("linkedAccounts") {
        @Override
        @SuppressWarnings("unchecked")
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.linkedAccounts(((Map<Object, Object>) map.get(getScope())).entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entriy -> entriy.getKey().toString(),
                            entriy -> entriy.getValue().toString())));
        }
    },
    /**
     * Körtagságok (itt az adott körnél a status csak egy értéket vehet fel, 
     * mégpedig a körvezető / tag / öregtag közül valamelyiket, ebben a prioritási sorrendben)
     * @see PersonEntitlement
     */
    EDU_PERSON_ENTILEMENT("eduPersonEntitlement") {
        @Override
        @SuppressWarnings("unchecked")
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.eduPersonEntitlement(((List<Map<Object, Object>>) map.get(getScope())).stream()
                    .map(entitlement -> new PersonEntitlement(
                            (Integer) entitlement.get("id"),
                            (String) entitlement.get("name"),
                            (String) entitlement.get("status"),
                            (List<String>) entitlement.get("title"),
                            (String) entitlement.get("start"),
                            (String) entitlement.get("end")))
                    .collect(Collectors.toList()));
        }
    },
    /**
     * Felhasználó szobaszáma (ha kollégista, akkor a kollégium neve és a szobaszám található 
     * meg benne, ha nem kollégista, akkor pedig null-t ad vissza). Amennyiben a felhasználó 
     * nem rendelkezik SCH Accounttal, szintén null-t ad eredményül. 
     * @deprecated Határozatlan ideig nem elérhető jogi okokból.
     */
    @Deprecated
    ROOM_NUMBER("roomNumber") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.roomNumber((String) map.get(getScope()));
        }
    },
    /**
     * Mobilszám a VIR-ből
     */
    MOBILE("mobile") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.mobile((String) map.get(getScope()));
        }
    },
    /**
     * Az adott félévben hallgatott tárgyak
     */
    COURSES("niifEduPersonAttendedCourse") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.courses(new ArrayList<>(Arrays.asList(((String) map.get(getScope())).split(";"))));
        }
    },
    /**
     * Közösségi belépők a VIR-ről, február és július között az őszi, egyébként (tehát 
     * augusztustól januárig) a tavaszi belépők
     * @see Entrant
     */
    ENTRANTS("entrants") {
        @Override
        @SuppressWarnings("unchecked")
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.entrants(((List<Map<Object, Object>>) map.get(getScope())).stream()
                    .map(entrant -> new Entrant(
                            (Integer) entrant.get("groupId"),
                            (String) entrant.get("groupName"),
                            (String) entrant.get("entrantType")))
                    .collect(Collectors.toList()));
        }
    },
    /**
     * Csoporttagságok a KSZK-s Active Directoryban
     */
    ACTIVE_DIRECTORY_MEMBERSHIP("admembership") {
        @Override
        @SuppressWarnings("unchecked")
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.admembership(((List<String>) map.get(getScope())));
        }
    },
    /**
     * Egyetemi jogviszony, jelenlegi lehetséges értékek: 
     * BME, BME_VIK, BME_VIK_ACTIVE, BME_VIK_NEWBIE
     * @see BMEUnitScope
     */
    BME_UNIT_SCOPE("bmeunitscope") {
        @Override
        @SuppressWarnings("unchecked")
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.bmeunitscope(((List<String>) map.get(getScope()))
                    .stream()
                    .map(BMEUnitScope::valueOf)
                    .collect(Collectors.toList()));
        }
    },
    /**
     * Állandó lakcím
     */
    PERMANENT_ADDRESS("permanentaddress") {
        @Override
        public void apply(ProfileDataResponseBuilder response, Map<String, Object> map) {
            response.permanentAddress((String) map.get(getScope()));
        }
    }
    ;

    @Getter
    private final String scope;
    
    Scope(String scope) {
        this.scope = scope;
    }

    public boolean canApply(Map<String, Object> map) {
        return map.containsKey(getScope());
    }
    
    public abstract void apply(ProfileDataResponseBuilder builder, Map<String, Object> map);
    
}

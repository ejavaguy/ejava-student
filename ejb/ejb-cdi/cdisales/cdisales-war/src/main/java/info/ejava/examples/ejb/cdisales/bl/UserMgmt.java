package info.ejava.examples.ejb.cdisales.bl;

import info.ejava.examples.ejb.cdisales.bo.Member;

public interface UserMgmt {
    Member createMember(Member member) throws InvalidAccount;
    Member getMember(int id);
    Member findMemberByLogin(String login);
}

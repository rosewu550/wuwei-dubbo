package com.wuwei.filestorage.storagetype.ladp;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * 描述：
 *
 * @author alphaxx
 * @since 2024/11/12 07:52
 */
public class LadpStorageClient {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // LDAP服务器连接配置
        System.out.println("请输入ladp服务器地址 (示例：ldap://192.168.1.1:389) : ");
        String ldapUrl = scanner.nextLine();
//        String ldapUrl = "ldap://192.168.8.105:389"; // LDAP服务器地址和端口
        System.out.println("请输入ladp根目录 (示例：D:/weaver/data, OU=data,OU=weaver,DC=D,DC=example,DC=com) : ");
        String baseDn = "dc=alphaxxcompany,dc=com";     // 基本DN，用于定义LDAP的起始目录
        String username = "cn=alphaxx,dc=alphaxxCompany,dc=com"; // LDAP管理员用户名
        String password = "10055pp"; // LDAP管理员密码
        String searchDn = "dc=alphaxxcompany,dc=com"; // 要查询的目录DN


        // 设置LDAP环境
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);


        try {
            // 创建LDAP连接上下文
            DirContext ctx = new InitialDirContext(env);
            System.out.println("连接LDAP服务器成功！");

            // 设置查询条件和返回属性
            // 查询所有权限对象（组和角色对象类包括groupOfNames, groupOfUniqueNames, posixGroup, organizationalRole等）
            String searchFilter = "(|(objectClass=groupOfNames)(objectClass=organizationalRole)(objectClass=posixGroup))";
            String[] attributes = {"cn", "member", "uniqueMember", "memberUid", "description"}; // 获取组名、成员和描述

            // 配置搜索控制
            SearchControls searchControls = new SearchControls();
//            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//            searchControls.setReturningAttributes(attributes);
            searchControls.setSearchScope(SearchControls.OBJECT_SCOPE); // 只查询当前对象
            searchControls.setReturningAttributes(new String[]{"ntSecurityDescriptor"}); // Active Directory的ACL属性

            // 执行查询
//            NamingEnumeration<SearchResult> results = ctx.search(baseDn, searchFilter, searchControls);
            NamingEnumeration<SearchResult> results = ctx.search(searchDn, "(objectClass=*)", searchControls);
            // 遍历查询结果
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                Attribute aclAttr = attrs.get("ntSecurityDescriptor");

                if (aclAttr != null) {
                    // 解析ACL信息
                    byte[] aclBytes = (byte[]) aclAttr.get();
                    System.out.println("解析ACL信息: " + new String(aclBytes));
//                    parseAcl(aclBytes); // 自定义解析ACL的逻辑
                } else {
                    System.out.println("该对象没有ntSecurityDescriptor属性。");
                }

//                System.out.println("DN: " + result.getNameInNamespace());
//                for (String attr : attributes) {
//                    Attribute attribute = attrs.get(attr);
//                    if (attribute != null) {
//                        System.out.println(attr + ": " + attribute);
//                    }
//                }
                System.out.println("-----------");
            }

            // 关闭上下文
            ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) throws Exception {
//        String ldapURL = "ldaps://machine.domain.com:636";
//        String username = "username"; // without @domain
//        String domainName = "domain.com";
//        String password = "password";
//        String timeout = "5000";
//        Hashtable<String, String> env = new Hashtable<>();
//        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        env.put("java.naming.security.sasl.realm", domainName);
//        env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
//        env.put(Context.SECURITY_PROTOCOL, "ssl");
//        env.put("javax.security.sasl.qop", "auth");
//        env.put(Context.PROVIDER_URL, ldapURL);
//        env.put(Context.SECURITY_PRINCIPAL, username);
//        env.put(Context.SECURITY_CREDENTIALS, password);
//        env.put(Context.REFERRAL, "ignore");
//        env.put("java.naming.ldap.version", "3");
//        env.put("com.sun.jndi.ldap.tls.cbtype", "tls-server-end-point");
//        env.put("com.sun.jndi.ldap.connect.pool", "true");
//        env.put("com.sun.jndi.ldap.connect.timeout", timeout);
//        LdapContext ctx = null;
//        try {
//            ctx = new InitialLdapContext(env, null);
//            System.out.println("Bind successful");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (ctx!=null) {
//                ctx.close();
//            }
//        }
//    }



}

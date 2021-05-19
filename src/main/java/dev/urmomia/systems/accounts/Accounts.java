package dev.urmomia.systems.accounts;

import dev.urmomia.systems.System;
import dev.urmomia.systems.Systems;
import dev.urmomia.systems.accounts.types.CrackedAccount;
import dev.urmomia.systems.accounts.types.PremiumAccount;
import dev.urmomia.systems.accounts.types.TheAlteningAccount;
import dev.urmomia.utils.misc.NbtException;
import dev.urmomia.utils.misc.NbtUtils;
import dev.urmomia.utils.network.MainExecutor;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Accounts extends System<Accounts> implements Iterable<Account<?>> {
    private List<Account<?>> accounts = new ArrayList<>();

    public Accounts() {
        super("accounts");
    }

    public static Accounts get() {
        return Systems.get(Accounts.class);
    }

    @Override
    public void init() {}

    public void add(Account<?> account) {
        accounts.add(account);
        save();
    }

    public boolean exists(Account<?> account) {
        return accounts.contains(account);
    }

    public void remove(Account<?> account) {
        if (accounts.remove(account)) {
            save();
        }
    }

    public int size() {
        return accounts.size();
    }

    @Override
    public Iterator<Account<?>> iterator() {
        return accounts.iterator();
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.put("accounts", NbtUtils.listToTag(accounts));

        return tag;
    }

    @Override
    public Accounts fromTag(CompoundTag tag) {
        MainExecutor.execute(() -> accounts = NbtUtils.listFromTag(tag.getList("accounts", 10), tag1 -> {
            CompoundTag t = (CompoundTag) tag1;
            if (!t.contains("type")) return null;

            AccountType type = AccountType.valueOf(t.getString("type"));

            try {
                Account<?> account = null;
                if (type == AccountType.Cracked) {
                    account = new CrackedAccount(null).fromTag(t);
                } else if (type == AccountType.Premium) {
                    account = new PremiumAccount(null, null).fromTag(t);
                } else if (type == AccountType.TheAltening) {
                    account = new TheAlteningAccount(null).fromTag(t);
                }

                if (account.fetchHead()) return account;
            } catch (NbtException e) {
                return null;
            }

            return null;
        }));

        return this;
    }
}
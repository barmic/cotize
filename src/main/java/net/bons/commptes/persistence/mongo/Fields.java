package net.bons.commptes.persistence.mongo;

public enum Fields {
    identifier(String.class),
    name(String.class),
    description(String.class),
    author(String.class),
    email(String.class),
    pass_admin(String.class),
    deal(String.class);

    private Class clazz;

    Fields(Class clazz) {
        this.clazz = clazz;
    }

    public Class getType() {
        return clazz;
    }

    public enum Deal {
        name(String.class),
        amount(Integer.class),
        date(String.class),
        email(String.class),
        creditor(String.class),
        debtor(String.class);

        private Class clazz;

        Deal(Class clazz) {
            this.clazz = clazz;
        }

        public Class getType() {
            return clazz;
        }
    }
}

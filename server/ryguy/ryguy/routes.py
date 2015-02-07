class JobletRoute(object):
    def db_for_read(self, model, **hints):
        if model._meta.app_label == 'jobs':
            return 'default'

        return None

    def db_for_write(self, model, **hints):
        if model._meta.app_label == 'jobs':
            return 'default'

        return None

    def allow_relation(self, obj1, obj2, **hints):
        if obj1._meta.app_label == 'jobs' or obj2._meta.app_label == 'jobs':
           return True

        return None

    def allow_migrate(self, db, model):
        if db == 'default':
            return model._meta.app_label == 'jobs'         
        elif model._meta.app_label == 'jobs':
            return False

        return None
package es.edn.groogle.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import es.edn.groogle.DriveService
import groovy.transform.CompileStatic

@CompileStatic
class PermissionsSpec implements DriveService.Permissions{

    Drive service
    File file
    protected List<Permission> current
    boolean strick = false

    protected void init(){
        current = []
        Drive.Permissions.List request = service.permissions().list(file.getId())
        request.fields="nextPageToken,permissions(*)"
        def response = request.execute()
        while (true) {
            current.addAll response.getPermissions()
            if(!response.nextPageToken)
                break
            request.pageToken = response.nextPageToken
            response = request.execute()
        }
    }

    private DriveService.Permissions newDesired(String type , String role, String[]values){
        values.each { String str->
            AssignRole ar = new AssignRole()
            ar.type = type
            ar.role = role
            ar.value = str
            listRoles.desired.add(ar)
        }
        this
    }

    @Override
    DriveService.Permissions usersAsReader(String... users) {
        newDesired('user','reader', users)
    }

    @Override
    DriveService.Permissions usersAsWriter(String... users) {
        newDesired('user','writer', users)
    }

    @Override
    DriveService.Permissions usersAsOrganizer(String... users) {
        newDesired('user','organizer', users)
    }

    @Override
    DriveService.Permissions domainAsReader(String domain) {
        newDesired('domain','reader', [domain] as String[])
    }

    @Override
    DriveService.Permissions domainAsWriter(String domain) {
        newDesired('domain','writer', [domain] as String[])
    }

    @Override
    DriveService.Permissions domainAsOrganizer(String domain) {
        newDesired('domain','organizer', [domain] as String[])
    }

    @Override
    DriveService.Permissions groupsAsReader(String... grps) {
        newDesired('group','reader', [grps.join(',')] as String[])
    }

    @Override
    DriveService.Permissions groupsAsWriter(String... grps) {
        newDesired('group','writer', [grps.join(',')] as String[])
    }

    @Override
    DriveService.Permissions groupsAsOrganizer(String... grps) {
        newDesired('group','organizer', [grps.join(',')] as String[])
    }


    @Override
    DriveService.Permissions anyoneAsReader() {
        newDesired('anyone','reader', [''] as String[])
    }

    @Override
    DriveService.Permissions anyoneAsWriter() {
        newDesired('anyone','writer', [''] as String[])
    }

    @Override
    DriveService.Permissions strick(boolean stricked) {
        this.strick = stricked
        this
    }

    private ListRoles listRoles = new ListRoles()

    class AssignRole{
        String type
        String value
        String role

        Object asType(Class clazz) {
            if (clazz == Permission) {
                Permission p = new Permission(type:type,role:role)
                if (['user', 'group'].contains(type))
                    p.emailAddress = value
                if(['domain'].contains(type))
                    p.domain = value
                return p
            }
        }
    }

    class ListRoles{
        List<AssignRole> desired = []

        List<AssignRole> toCreate(List<Permission>current){
            List<AssignRole> ret = []
            desired.each{ d->
                def exist = current.find{ p->
                    if( p.type == d.type && p.role == d.role ) {
                        if (['user', 'group'].contains(d.type)) {
                            return d.value.split(',') == "$p.emailAddress".toString().split(',')
                        }
                        if(['anyone'].contains(d.type)){
                            return true
                        }
                        if(['domain'].contains(d.type)){
                            return d.value == p.domain
                        }
                    }
                    false
                }
                if(!exist)
                    ret.add d
            }
            ret
        }

        List<Permission> toRemove(List<Permission>current){
            List<Permission> ret = []
            current.each{ p->
                if( p.role == 'owner' )
                    return
                def exist = desired.find{ d->
                    if( p.type == d.type && p.role == d.role ) {
                        if (['user', 'group'].contains(d.type)) {
                            return d.value.split(',') == "$p.emailAddress".toString().split(',')
                        }
                        if(['anyone'].contains(d.type)){
                            return true
                        }
                        if(['domain'].contains(d.type)){
                            return d.value == p.domain
                        }
                    }
                    false
                }
                if(!exist)
                    ret.add p
            }
            ret
        }

    }

    protected void execute(){
        def toRemove = listRoles.toRemove(current)
        if(strick) {
            toRemove.each {
                service.permissions().delete(file.getId(), it.getId()).execute()
            }
        }
        listRoles.toCreate(current).each{
            Permission p = it as Permission
            service.permissions().create(file.getId(), p).execute()
        }

        init()
    }
}

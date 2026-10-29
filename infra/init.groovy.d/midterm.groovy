import jenkins.model.Jenkins
import hudson.security.*
import hudson.model.Node
import hudson.slaves.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.*
def j = Jenkins.get()
def realm = new HudsonPrivateSecurityRealm(false)
realm.createAccount('amir', new File('/run/secrets/admin-password').text.trim())
j.setSecurityRealm(realm)
def auth = new FullControlOnceLoggedInAuthorizationStrategy()
auth.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(auth)
j.setNumExecutors(0)
if (j.getNode('sembi-amir-agent') == null) {
    def node = new DumbSlave('sembi-amir-agent', 'Dedicated Sembi Amir JDK21 and Docker agent', '/home/jenkins/agent', '1', Node.Mode.EXCLUSIVE, 'sembi-amir-docker', new JNLPLauncher(), RetentionStrategy.INSTANCE, [])
    j.addNode(node)
}
new File('/bootstrap/agent-secret').text = j.getNode('sembi-amir-agent').toComputer().getJnlpMac()
def job=j.getItem('Sembi_Amir_Student_API') ?: j.createProject(WorkflowJob, 'Sembi_Amir_Student_API')
def scm=new GitSCM([new UserRemoteConfig('https://github.com/s1mbaq/sembi-amir-student-api.git', null, null, null)], [new BranchSpec('*/main')], false, [], null, null, [])
job.setDefinition(new CpsScmFlowDefinition(scm, 'Jenkinsfile'))
job.save()
j.save()

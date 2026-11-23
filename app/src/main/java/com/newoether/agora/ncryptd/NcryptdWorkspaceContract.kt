package com.newoether.agora.ncryptd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transport-neutral boundary to nCryptd. Implementations must use the signed-in
 * user's access token and server-side RLS; never embed privileged credentials.
 */
interface NcryptdWorkspaceApi {
    suspend fun getCurrentUser(): NcryptdUser
    suspend fun listWorkspaces(cursor: String? = null): NcryptdPage<NcryptdWorkspace>
    suspend fun listProjects(workspaceId: String, cursor: String? = null): NcryptdPage<NcryptdProject>
    suspend fun listIssues(projectId: String, cursor: String? = null): NcryptdPage<NcryptdIssue>
    suspend fun getIssue(issueId: String): NcryptdIssue
}

@Serializable
data class NcryptdPage<T>(
    val items: List<T>,
    val nextCursor: String? = null,
)

@Serializable
data class NcryptdUser(
    val id: String,
    val email: String? = null,
)

@Serializable
data class NcryptdWorkspace(
    val id: String,
    val name: String,
)

@Serializable
data class NcryptdMembership(
    @SerialName("workspace_id") val workspaceId: String,
    @SerialName("user_id") val userId: String,
    val role: String,
)

@Serializable
data class NcryptdProject(
    val id: String,
    @SerialName("workspace_id") val workspaceId: String,
    val name: String,
    val description: String = "",
    val status: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class NcryptdIssue(
    val id: String,
    @SerialName("project_id") val projectId: String,
    @SerialName("issue_key") val issueKey: String,
    val title: String,
    val description: String = "",
    val type: String,
    val status: String,
    val priority: String,
    val assignee: String = "",
    @SerialName("due_date") val dueDate: String? = null,
    val estimate: Int = 0,
)

@Serializable
data class NcryptdApiError(
    val code: String,
    val message: String,
    @SerialName("request_id") val requestId: String? = null,
)

/**
 * Supabase Auth/PostgREST resources used by the nCryptd web client.
 * Query filters and relationship aliases must be verified against deployed schema.
 */
object NcryptdSupabaseResources {
    const val AUTH_USER = "/auth/v1/user"
    const val WORKSPACE_MEMBERS = "/rest/v1/workspace_members"
    const val WORKSPACES = "/rest/v1/workspaces"
    const val PROJECTS = "/rest/v1/projects"
    const val ISSUES = "/rest/v1/issues"
    const val PROFILES = "/rest/v1/profiles"
    const val ADD_WORKSPACE_MEMBER = "/rest/v1/rpc/add_workspace_member"
}

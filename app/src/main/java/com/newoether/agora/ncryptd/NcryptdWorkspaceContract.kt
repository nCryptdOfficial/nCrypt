package com.newoether.agora.ncryptd

import kotlinx.serialization.Serializable

/**
 * Wire contract for the nCryptd workspace service.
 *
 * This is deliberately a transport contract, not a second local source of truth.
 * Implementations must obtain a short-lived user access token from the authenticated
 * session provider and must never embed a service-role key in the Android client.
 */
interface NcryptdWorkspaceApi {
    suspend fun getCurrentUser(): NcryptdUser
    suspend fun listWorkspaces(cursor: String? = null): NcryptdPage<NcryptdWorkspace>
    suspend fun listProjects(workspaceId: String, cursor: String? = null): NcryptdPage<NcryptdProject>
    suspend fun listTasks(projectId: String, cursor: String? = null): NcryptdPage<NcryptdTask>
    suspend fun getTask(taskId: String): NcryptdTask
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
    val displayName: String? = null,
)

@Serializable
data class NcryptdWorkspace(
    val id: String,
    val name: String,
    val role: String,
)

@Serializable
data class NcryptdProject(
    val id: String,
    val workspaceId: String,
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class NcryptdTask(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String? = null,
    val assigneeId: String? = null,
    val dueAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class NcryptdApiError(
    val code: String,
    val message: String,
    val requestId: String? = null,
)

/**
 * Supabase Auth/PostgREST resources already used by the nCryptd web client.
 * A mobile implementation must use the signed-in user's access token and RLS.
 */
object NcryptdSupabaseResources {
    const val AUTH_USER = "/auth/v1/user"
    const val WORKSPACE_MEMBERS = "/rest/v1/workspace_members"
    const val WORKSPACES = "/rest/v1/workspaces"
    const val PROJECTS = "/rest/v1/projects"
    const val ISSUES = "/rest/v1/issues"
    const val PROFILES = "/rest/v1/profiles"
}

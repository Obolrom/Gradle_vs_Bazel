package com.romix.feature.feat232

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat232Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat232UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat232FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat232UserSummary
)

data class Feat232UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat232NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat232Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat232Config = Feat232Config()
) {

    fun loadSnapshot(userId: Long): Feat232NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat232NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat232UserSummary {
        return Feat232UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat232FeedItem> {
        val result = java.util.ArrayList<Feat232FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat232FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat232UiMapper {

    fun mapToUi(model: List<Feat232FeedItem>): Feat232UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat232UiModel(
            header = UiText("Feat232 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat232UiModel =
        Feat232UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat232UiModel =
        Feat232UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat232UiModel =
        Feat232UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat232Service(
    private val repository: Feat232Repository,
    private val uiMapper: Feat232UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat232UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat232UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat232UserItem1(val user: CoreUser, val label: String)
data class Feat232UserItem2(val user: CoreUser, val label: String)
data class Feat232UserItem3(val user: CoreUser, val label: String)
data class Feat232UserItem4(val user: CoreUser, val label: String)
data class Feat232UserItem5(val user: CoreUser, val label: String)
data class Feat232UserItem6(val user: CoreUser, val label: String)
data class Feat232UserItem7(val user: CoreUser, val label: String)
data class Feat232UserItem8(val user: CoreUser, val label: String)
data class Feat232UserItem9(val user: CoreUser, val label: String)
data class Feat232UserItem10(val user: CoreUser, val label: String)

data class Feat232StateBlock1(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock2(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock3(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock4(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock5(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock6(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock7(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock8(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock9(val state: Feat232UiModel, val checksum: Int)
data class Feat232StateBlock10(val state: Feat232UiModel, val checksum: Int)

fun buildFeat232UserItem(user: CoreUser, index: Int): Feat232UserItem1 {
    return Feat232UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat232StateBlock(model: Feat232UiModel): Feat232StateBlock1 {
    return Feat232StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat232UserSummary> {
    val list = java.util.ArrayList<Feat232UserSummary>(users.size)
    for (user in users) {
        list += Feat232UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat232UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat232UiModel {
    val summaries = (0 until count).map {
        Feat232UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat232UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat232UiModel> {
    val models = java.util.ArrayList<Feat232UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat232AnalyticsEvent1(val name: String, val value: String)
data class Feat232AnalyticsEvent2(val name: String, val value: String)
data class Feat232AnalyticsEvent3(val name: String, val value: String)
data class Feat232AnalyticsEvent4(val name: String, val value: String)
data class Feat232AnalyticsEvent5(val name: String, val value: String)
data class Feat232AnalyticsEvent6(val name: String, val value: String)
data class Feat232AnalyticsEvent7(val name: String, val value: String)
data class Feat232AnalyticsEvent8(val name: String, val value: String)
data class Feat232AnalyticsEvent9(val name: String, val value: String)
data class Feat232AnalyticsEvent10(val name: String, val value: String)

fun logFeat232Event1(event: Feat232AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat232Event2(event: Feat232AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat232Event3(event: Feat232AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat232Event4(event: Feat232AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat232Event5(event: Feat232AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat232Event6(event: Feat232AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat232Event7(event: Feat232AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat232Event8(event: Feat232AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat232Event9(event: Feat232AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat232Event10(event: Feat232AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat232Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat232Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat232(u: CoreUser): Feat232Projection1 =
    Feat232Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat232Projection1> {
    val list = java.util.ArrayList<Feat232Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat232(u)
    }
    return list
}

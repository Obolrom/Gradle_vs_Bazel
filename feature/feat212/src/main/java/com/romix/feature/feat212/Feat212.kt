package com.romix.feature.feat212

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat212Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat212UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat212FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat212UserSummary
)

data class Feat212UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat212NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat212Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat212Config = Feat212Config()
) {

    fun loadSnapshot(userId: Long): Feat212NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat212NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat212UserSummary {
        return Feat212UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat212FeedItem> {
        val result = java.util.ArrayList<Feat212FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat212FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat212UiMapper {

    fun mapToUi(model: List<Feat212FeedItem>): Feat212UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat212UiModel(
            header = UiText("Feat212 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat212UiModel =
        Feat212UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat212UiModel =
        Feat212UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat212UiModel =
        Feat212UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat212Service(
    private val repository: Feat212Repository,
    private val uiMapper: Feat212UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat212UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat212UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat212UserItem1(val user: CoreUser, val label: String)
data class Feat212UserItem2(val user: CoreUser, val label: String)
data class Feat212UserItem3(val user: CoreUser, val label: String)
data class Feat212UserItem4(val user: CoreUser, val label: String)
data class Feat212UserItem5(val user: CoreUser, val label: String)
data class Feat212UserItem6(val user: CoreUser, val label: String)
data class Feat212UserItem7(val user: CoreUser, val label: String)
data class Feat212UserItem8(val user: CoreUser, val label: String)
data class Feat212UserItem9(val user: CoreUser, val label: String)
data class Feat212UserItem10(val user: CoreUser, val label: String)

data class Feat212StateBlock1(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock2(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock3(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock4(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock5(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock6(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock7(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock8(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock9(val state: Feat212UiModel, val checksum: Int)
data class Feat212StateBlock10(val state: Feat212UiModel, val checksum: Int)

fun buildFeat212UserItem(user: CoreUser, index: Int): Feat212UserItem1 {
    return Feat212UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat212StateBlock(model: Feat212UiModel): Feat212StateBlock1 {
    return Feat212StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat212UserSummary> {
    val list = java.util.ArrayList<Feat212UserSummary>(users.size)
    for (user in users) {
        list += Feat212UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat212UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat212UiModel {
    val summaries = (0 until count).map {
        Feat212UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat212UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat212UiModel> {
    val models = java.util.ArrayList<Feat212UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat212AnalyticsEvent1(val name: String, val value: String)
data class Feat212AnalyticsEvent2(val name: String, val value: String)
data class Feat212AnalyticsEvent3(val name: String, val value: String)
data class Feat212AnalyticsEvent4(val name: String, val value: String)
data class Feat212AnalyticsEvent5(val name: String, val value: String)
data class Feat212AnalyticsEvent6(val name: String, val value: String)
data class Feat212AnalyticsEvent7(val name: String, val value: String)
data class Feat212AnalyticsEvent8(val name: String, val value: String)
data class Feat212AnalyticsEvent9(val name: String, val value: String)
data class Feat212AnalyticsEvent10(val name: String, val value: String)

fun logFeat212Event1(event: Feat212AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat212Event2(event: Feat212AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat212Event3(event: Feat212AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat212Event4(event: Feat212AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat212Event5(event: Feat212AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat212Event6(event: Feat212AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat212Event7(event: Feat212AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat212Event8(event: Feat212AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat212Event9(event: Feat212AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat212Event10(event: Feat212AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat212Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat212Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat212(u: CoreUser): Feat212Projection1 =
    Feat212Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat212Projection1> {
    val list = java.util.ArrayList<Feat212Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat212(u)
    }
    return list
}

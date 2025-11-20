package com.romix.feature.feat601

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat601Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat601UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat601FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat601UserSummary
)

data class Feat601UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat601NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat601Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat601Config = Feat601Config()
) {

    fun loadSnapshot(userId: Long): Feat601NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat601NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat601UserSummary {
        return Feat601UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat601FeedItem> {
        val result = java.util.ArrayList<Feat601FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat601FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat601UiMapper {

    fun mapToUi(model: List<Feat601FeedItem>): Feat601UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat601UiModel(
            header = UiText("Feat601 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat601UiModel =
        Feat601UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat601UiModel =
        Feat601UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat601UiModel =
        Feat601UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat601Service(
    private val repository: Feat601Repository,
    private val uiMapper: Feat601UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat601UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat601UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat601UserItem1(val user: CoreUser, val label: String)
data class Feat601UserItem2(val user: CoreUser, val label: String)
data class Feat601UserItem3(val user: CoreUser, val label: String)
data class Feat601UserItem4(val user: CoreUser, val label: String)
data class Feat601UserItem5(val user: CoreUser, val label: String)
data class Feat601UserItem6(val user: CoreUser, val label: String)
data class Feat601UserItem7(val user: CoreUser, val label: String)
data class Feat601UserItem8(val user: CoreUser, val label: String)
data class Feat601UserItem9(val user: CoreUser, val label: String)
data class Feat601UserItem10(val user: CoreUser, val label: String)

data class Feat601StateBlock1(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock2(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock3(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock4(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock5(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock6(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock7(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock8(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock9(val state: Feat601UiModel, val checksum: Int)
data class Feat601StateBlock10(val state: Feat601UiModel, val checksum: Int)

fun buildFeat601UserItem(user: CoreUser, index: Int): Feat601UserItem1 {
    return Feat601UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat601StateBlock(model: Feat601UiModel): Feat601StateBlock1 {
    return Feat601StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat601UserSummary> {
    val list = java.util.ArrayList<Feat601UserSummary>(users.size)
    for (user in users) {
        list += Feat601UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat601UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat601UiModel {
    val summaries = (0 until count).map {
        Feat601UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat601UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat601UiModel> {
    val models = java.util.ArrayList<Feat601UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat601AnalyticsEvent1(val name: String, val value: String)
data class Feat601AnalyticsEvent2(val name: String, val value: String)
data class Feat601AnalyticsEvent3(val name: String, val value: String)
data class Feat601AnalyticsEvent4(val name: String, val value: String)
data class Feat601AnalyticsEvent5(val name: String, val value: String)
data class Feat601AnalyticsEvent6(val name: String, val value: String)
data class Feat601AnalyticsEvent7(val name: String, val value: String)
data class Feat601AnalyticsEvent8(val name: String, val value: String)
data class Feat601AnalyticsEvent9(val name: String, val value: String)
data class Feat601AnalyticsEvent10(val name: String, val value: String)

fun logFeat601Event1(event: Feat601AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat601Event2(event: Feat601AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat601Event3(event: Feat601AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat601Event4(event: Feat601AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat601Event5(event: Feat601AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat601Event6(event: Feat601AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat601Event7(event: Feat601AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat601Event8(event: Feat601AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat601Event9(event: Feat601AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat601Event10(event: Feat601AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat601Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat601Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat601(u: CoreUser): Feat601Projection1 =
    Feat601Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat601Projection1> {
    val list = java.util.ArrayList<Feat601Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat601(u)
    }
    return list
}

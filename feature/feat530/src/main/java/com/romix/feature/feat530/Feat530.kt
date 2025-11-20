package com.romix.feature.feat530

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat530Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat530UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat530FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat530UserSummary
)

data class Feat530UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat530NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat530Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat530Config = Feat530Config()
) {

    fun loadSnapshot(userId: Long): Feat530NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat530NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat530UserSummary {
        return Feat530UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat530FeedItem> {
        val result = java.util.ArrayList<Feat530FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat530FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat530UiMapper {

    fun mapToUi(model: List<Feat530FeedItem>): Feat530UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat530UiModel(
            header = UiText("Feat530 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat530UiModel =
        Feat530UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat530UiModel =
        Feat530UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat530UiModel =
        Feat530UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat530Service(
    private val repository: Feat530Repository,
    private val uiMapper: Feat530UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat530UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat530UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat530UserItem1(val user: CoreUser, val label: String)
data class Feat530UserItem2(val user: CoreUser, val label: String)
data class Feat530UserItem3(val user: CoreUser, val label: String)
data class Feat530UserItem4(val user: CoreUser, val label: String)
data class Feat530UserItem5(val user: CoreUser, val label: String)
data class Feat530UserItem6(val user: CoreUser, val label: String)
data class Feat530UserItem7(val user: CoreUser, val label: String)
data class Feat530UserItem8(val user: CoreUser, val label: String)
data class Feat530UserItem9(val user: CoreUser, val label: String)
data class Feat530UserItem10(val user: CoreUser, val label: String)

data class Feat530StateBlock1(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock2(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock3(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock4(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock5(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock6(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock7(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock8(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock9(val state: Feat530UiModel, val checksum: Int)
data class Feat530StateBlock10(val state: Feat530UiModel, val checksum: Int)

fun buildFeat530UserItem(user: CoreUser, index: Int): Feat530UserItem1 {
    return Feat530UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat530StateBlock(model: Feat530UiModel): Feat530StateBlock1 {
    return Feat530StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat530UserSummary> {
    val list = java.util.ArrayList<Feat530UserSummary>(users.size)
    for (user in users) {
        list += Feat530UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat530UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat530UiModel {
    val summaries = (0 until count).map {
        Feat530UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat530UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat530UiModel> {
    val models = java.util.ArrayList<Feat530UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat530AnalyticsEvent1(val name: String, val value: String)
data class Feat530AnalyticsEvent2(val name: String, val value: String)
data class Feat530AnalyticsEvent3(val name: String, val value: String)
data class Feat530AnalyticsEvent4(val name: String, val value: String)
data class Feat530AnalyticsEvent5(val name: String, val value: String)
data class Feat530AnalyticsEvent6(val name: String, val value: String)
data class Feat530AnalyticsEvent7(val name: String, val value: String)
data class Feat530AnalyticsEvent8(val name: String, val value: String)
data class Feat530AnalyticsEvent9(val name: String, val value: String)
data class Feat530AnalyticsEvent10(val name: String, val value: String)

fun logFeat530Event1(event: Feat530AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat530Event2(event: Feat530AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat530Event3(event: Feat530AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat530Event4(event: Feat530AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat530Event5(event: Feat530AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat530Event6(event: Feat530AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat530Event7(event: Feat530AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat530Event8(event: Feat530AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat530Event9(event: Feat530AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat530Event10(event: Feat530AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat530Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat530Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat530(u: CoreUser): Feat530Projection1 =
    Feat530Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat530Projection1> {
    val list = java.util.ArrayList<Feat530Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat530(u)
    }
    return list
}

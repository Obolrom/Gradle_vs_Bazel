package com.romix.feature.feat91

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat91Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat91UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat91FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat91UserSummary
)

data class Feat91UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat91NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat91Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat91Config = Feat91Config()
) {

    fun loadSnapshot(userId: Long): Feat91NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat91NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat91UserSummary {
        return Feat91UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat91FeedItem> {
        val result = java.util.ArrayList<Feat91FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat91FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat91UiMapper {

    fun mapToUi(model: List<Feat91FeedItem>): Feat91UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat91UiModel(
            header = UiText("Feat91 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat91UiModel =
        Feat91UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat91UiModel =
        Feat91UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat91UiModel =
        Feat91UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat91Service(
    private val repository: Feat91Repository,
    private val uiMapper: Feat91UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat91UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat91UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat91UserItem1(val user: CoreUser, val label: String)
data class Feat91UserItem2(val user: CoreUser, val label: String)
data class Feat91UserItem3(val user: CoreUser, val label: String)
data class Feat91UserItem4(val user: CoreUser, val label: String)
data class Feat91UserItem5(val user: CoreUser, val label: String)
data class Feat91UserItem6(val user: CoreUser, val label: String)
data class Feat91UserItem7(val user: CoreUser, val label: String)
data class Feat91UserItem8(val user: CoreUser, val label: String)
data class Feat91UserItem9(val user: CoreUser, val label: String)
data class Feat91UserItem10(val user: CoreUser, val label: String)

data class Feat91StateBlock1(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock2(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock3(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock4(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock5(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock6(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock7(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock8(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock9(val state: Feat91UiModel, val checksum: Int)
data class Feat91StateBlock10(val state: Feat91UiModel, val checksum: Int)

fun buildFeat91UserItem(user: CoreUser, index: Int): Feat91UserItem1 {
    return Feat91UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat91StateBlock(model: Feat91UiModel): Feat91StateBlock1 {
    return Feat91StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat91UserSummary> {
    val list = java.util.ArrayList<Feat91UserSummary>(users.size)
    for (user in users) {
        list += Feat91UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat91UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat91UiModel {
    val summaries = (0 until count).map {
        Feat91UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat91UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat91UiModel> {
    val models = java.util.ArrayList<Feat91UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat91AnalyticsEvent1(val name: String, val value: String)
data class Feat91AnalyticsEvent2(val name: String, val value: String)
data class Feat91AnalyticsEvent3(val name: String, val value: String)
data class Feat91AnalyticsEvent4(val name: String, val value: String)
data class Feat91AnalyticsEvent5(val name: String, val value: String)
data class Feat91AnalyticsEvent6(val name: String, val value: String)
data class Feat91AnalyticsEvent7(val name: String, val value: String)
data class Feat91AnalyticsEvent8(val name: String, val value: String)
data class Feat91AnalyticsEvent9(val name: String, val value: String)
data class Feat91AnalyticsEvent10(val name: String, val value: String)

fun logFeat91Event1(event: Feat91AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat91Event2(event: Feat91AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat91Event3(event: Feat91AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat91Event4(event: Feat91AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat91Event5(event: Feat91AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat91Event6(event: Feat91AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat91Event7(event: Feat91AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat91Event8(event: Feat91AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat91Event9(event: Feat91AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat91Event10(event: Feat91AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat91Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat91Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat91(u: CoreUser): Feat91Projection1 =
    Feat91Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat91Projection1> {
    val list = java.util.ArrayList<Feat91Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat91(u)
    }
    return list
}
